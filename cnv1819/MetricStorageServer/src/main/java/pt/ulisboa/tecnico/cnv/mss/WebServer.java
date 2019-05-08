package pt.ulisboa.tecnico.cnv.mss;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public class WebServer {
    private static MetricStorageManager mss;

    public static void main(String[] args) throws Exception {

        init();

        final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println(server.getAddress().toString());
        server.createContext("/requestmetric", new RequestMetricHandler());
        server.createContext("/putmetric", new PutMetricHandler());
    }

    private static void init() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("eu-central-1")
                .build();

        mss = new MetricStorageManager(dynamoDB);
    }

    private static class RequestMetricHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {

            try {
                final String query = t.getRequestURI().getQuery();

                System.out.println(">Query:\t" + query);

                final String[] params = query.split("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class PutMetricHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {
            try{
                final String query = t.getRequestURI().getQuery();
                System.out.println(">Query:\t" + query);

                //make put

                String response = "";
                t.sendResponseHeaders(200, response.length());
                final OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}