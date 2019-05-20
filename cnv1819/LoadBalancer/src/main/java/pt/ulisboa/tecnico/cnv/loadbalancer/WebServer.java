package pt.ulisboa.tecnico.cnv.loadbalancer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpResponse;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;
import pt.ulisboa.tecnico.cnv.common.Common;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;


public class WebServer {
    private static LoadBalancer loadBalancer;
    private static AmazonEC2 ec2;

    public static void main(String[] args) throws Exception {

        System.out.println("************ Launching Web Server ************");
        try{
            init();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error while initializing LoadBalancer");
            System.exit(0);
        }

        final HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println(server.getAddress().toString());

        server.createContext("/climb", new RequestHandler());
        server.createContext("/instances", new ListInstancesHandler());
        server.createContext("/test", new TestHandler());
    }

    private static void init() throws AmazonClientException {
        AWSCredentials credentials;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard().withRegion("eu-central-1").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
       
        loadBalancer = new LoadBalancer(ec2);
    }

    private static class RequestHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {
            try {

                String query = t.getRequestURI().getQuery();
                //decide the worker that should do the work

                double cost = loadBalancer.requestMetricMss(Common.argumentsFromQuery(query));
                System.out.println("CUUUUSTO: " + cost);
            
                HttpAnswer response = null;
                String workerIp = null;
                InstanceInfo instanceInfo = null;
                int jobId = -1;

                while (response == null ){
                    try{
                        String queryForWorker = query;
                        instanceInfo = loadBalancer.whichWorker();
                        System.out.println("instanceinfo: " + instanceInfo);
                       
                        Job job = new Job(instanceInfo.getInstance(), cost);
                        jobId = instanceInfo.addJob(job);
                        job.setId(jobId);

                        queryForWorker += "&c=" + cost + "&id=" + jobId;
                        System.out.println(instanceInfo.getTotalCost());
                        
                        workerIp = instanceInfo.getInstance().getPublicIpAddress();

                        System.out.println(">Job sent to :\t" + workerIp);
                        response = HttpRequest.redirectURL("http://" + workerIp + ":8000/climb?", queryForWorker);
                    } catch(Exception e) {
                        System.out.printf("Worker at %s probably crashed!", workerIp);
                        loadBalancer.instanceInfoMap.remove(workerIp);

                        //if webserver crahsed and the instance is running
                        if (instanceInfo.getInstance().getState().getCode() == 16) {
                            synchronized(loadBalancer.toDeleteLock) {
                                instanceInfo.setToDelete(true);
                                List<InstanceStateChange> stateChange;
                                do {
                                   stateChange = loadBalancer.instanceManager.terminateInstances(instanceInfo);
                                } while (stateChange == null);
                                loadBalancer.instanceInfoMap.remove(instanceInfo.getInstance().getPublicIpAddress());
                            }
                        }
                        
                    }
                }
                
                instanceInfo.removeJob(jobId);

                // Send response to browser.
                final Headers hdrs = t.getResponseHeaders();
                t.sendResponseHeaders(200, response.getResponse().length);
                hdrs.add("Content-Type", "image/png");

                hdrs.add("Access-Control-Allow-Origin", "*");
                hdrs.add("Access-Control-Allow-Credentials", "true");
                hdrs.add("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
                hdrs.add("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

                final OutputStream os = t.getResponseBody();
                os.write(response.getResponse());
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ListInstancesHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {
            try {
                List<String> instances = loadBalancer.listWorkers();
                String response = instances.toString();
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TestHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {
            final String query = t.getRequestURI().getQuery();

            try {
                List<String> workers = loadBalancer.listWorkers();

                
                String response = "List of active instances\n";
                for (String worker : workers) {
                    response += worker + "\n";
                }  

                System.out.println("COST $: " + loadBalancer.requestMetricMss(Common.argumentsFromQuery(query)));

                t.sendResponseHeaders(200, response.length());
                System.out.println(response);

                System.out.println(loadBalancer.instanceInfoMap);

                
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes()); 
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
