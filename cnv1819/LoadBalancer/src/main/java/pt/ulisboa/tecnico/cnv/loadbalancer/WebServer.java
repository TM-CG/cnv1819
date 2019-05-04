package pt.ulisboa.tecnico.cnv.loadbalancer;

import java.net.InetSocketAddress;
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

    final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    server.setExecutor(Executors.newCachedThreadPool());
    server.start();

    System.out.println(server.getAddress().toString());
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(final HttpExchange t) {

      try{
        final String query = t.getRequestURI().getQuery();

        System.out.println(">Query:\t" + query);

        final String[] params = query.split("&");

        //metrics
        /* Recebe o request dos clientes e adiciona o id 
        necessário para a tabela do dynamo, decide qual o
        melhor worker para mandar o pedido para e envia juntamente
        com o id extra */
      }catch (Exception e){}
    }
    
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
    ec2 = AmazonEC2ClientBuilder.standard().withRegion("eu-west-2").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    cloudWatch = AmazonCloudWatchClientBuilder.standard().withRegion("eu-west-2").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    loadBalancer = new LoadBalancer();
  }
}