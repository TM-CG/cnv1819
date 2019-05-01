package pt.ulisboa.tecnico.cnv.aws;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WebServer {

  static LoadBalancer loadBalancer;

  public static void main(String[] args) throws Exception {
   
    loadBalancer = new LoadBalancer();

    final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    server.setExecutor(Executors.newCachedThreadPool());
    server.start();

    System.out.println(server.getAddress().toString())
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(final HttpExchange t) {

        try{
          final String query = t.getRequestURI().getQuery();

          System.out.println(">Query:\t" + query);

          final String[] params = query.split("&");

          String publicAddress = loadBalancer.selectWorker();

          
          //metrics
          /* Recebe o request dos clientes e adiciona o id 
          necessário para a tabela do dynamo, decide qual o
          melhor worker para mandar o pedido para e envia juntamente
          com o id extra */
        }
      }
    
    }
  }
}