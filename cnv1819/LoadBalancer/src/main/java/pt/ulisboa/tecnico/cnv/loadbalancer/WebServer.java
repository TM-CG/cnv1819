package pt.ulisboa.tecnico.cnv.loadbalancer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;


public class WebServer {
  private static LoadBalancer loadBalancer;
  private static AmazonEC2 ec2;
  private static AmazonCloudWatch cloudWatch;

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
  }

  private static void init() throws AmazonClientException {
    AWSCredentials credentials = null;
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
    cloudWatch = AmazonCloudWatchClientBuilder.standard().withRegion("eu-central-1").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    loadBalancer = new LoadBalancer(ec2);
  }

  private static class RequestHandler implements HttpHandler {
    @Override
    public void handle(final HttpExchange t) {
      try {

        final String query = t.getRequestURI().getQuery();
        String workerIp = "35.156.23.222:8000";
        HttpRequest.redirectURL(workerIp, t.getRequestURI().toString());

        System.out.println(">Sent to :\t" + workerIp);

        //metrics
        /* Recebe o request dos clientes e adiciona o id¢ﬂ
        necessário para a tabela do dynamo, decide qual o
        melhor worker para mandar o pedido para e envia juntamente
        com o id extra */
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
}