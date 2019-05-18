package pt.ulisboa.tecnico.cnv.mss;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import pt.ulisboa.tecnico.cnv.common.Common;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

public class WebServer {
    private static MetricStorageManager mss;

    private static final Object id_lock = new Object();
    private static int identifier = 0;

    public static void main(String[] args) throws Exception {
        init();

        final HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);

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

                System.out.println(mss.getMetrics(query));
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

    private static class PutMetricHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) {
            try{
                final String query = t.getRequestURI().getQuery();
                System.out.println(">Query:\t" + query);

                Map<String, String> arguments = Common.argumentsFromQuery(query);
                Metrics metric  = Common.metricFromArguments(arguments);
                mss.addMetricObject(MetricStorageManager.TBL_NAME, metric.toStringForId(), metric);

                String response = "METRIC INSERTED IN DYNAMODB";
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