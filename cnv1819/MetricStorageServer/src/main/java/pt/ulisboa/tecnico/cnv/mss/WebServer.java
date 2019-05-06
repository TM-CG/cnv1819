package pt.ulisboa.tecnico.cnv.mss;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public class WebServer {
  public static void main(String[] args) throws Exception {

    final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    server.setExecutor(Executors.newCachedThreadPool());
    server.start();

    System.out.println(server.getAddress().toString());

    server.createContext("/metric", new RequestHandler());
  }

  private static class RequestHandler implements HttpHandler {
    @Override
    public void handle(final HttpExchange t) {

      try {
        final String query = t.getRequestURI().getQuery();

        System.out.println(">Query:\t" + query);

        final String[] params = query.split("&");

        //metrics
        /* Recebe o request dos clientes e adiciona o id
        necessário para a tabela do dynamo, decide qual o
        melhor worker para mandar o pedido para e envia juntamente
        com o id extra */
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}