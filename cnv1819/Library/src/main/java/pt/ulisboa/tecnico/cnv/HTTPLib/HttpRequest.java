package pt.ulisboa.tecnico.cnv.HTTPLib;

import com.amazonaws.http.apache.request.impl.HttpGetWithBody;
import com.amazonaws.services.waf.model.HTTPRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpRequest {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static HttpAnswer redirectURL(String host, String url) {
        url = host + url;
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            return sendGet(con);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HttpAnswer(400, "");
    }

    public static HttpAnswer sendHttpRequest(String url, Map<String, String> arguments) {

        if (arguments.size() > 0 ){
            url += "?";
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                url+= entry.getKey() + "=" + entry.getValue() + "&";
            }
        }
        System.out.println(url);
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; //TODO
    }

    private static HttpAnswer sendGet(HttpURLConnection con) {
        try{
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            OutputStream os = new FileOutputStream("request.png");
            int responseCode = con.getResponseCode();
            InputStream in = con.getInputStream();

            byte[] bytes = new byte[2048];
            int length;
            while ((length = in.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            in.close();

            return new HttpAnswer(responseCode, "pls");
        }catch (IOException e){
            return new HttpAnswer(400, "Error");
        }
    }
}
