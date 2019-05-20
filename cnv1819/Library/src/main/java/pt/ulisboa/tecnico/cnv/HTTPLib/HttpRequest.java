package pt.ulisboa.tecnico.cnv.HTTPLib;

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

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HttpAnswer(400, null);
    }

    public static HttpAnswer sendHttpRequest(String url, Map<String, String> arguments) {
	    StringBuilder newUrl = new StringBuilder();

        if (arguments.size() > 0 ){
            newUrl.append(url);
            newUrl.append("?");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
		        newUrl.append(entry.getKey());
                newUrl.append("=");
                newUrl.append(entry.getValue());
                newUrl.append("&");
            }
	        newUrl.deleteCharAt(newUrl.length() - 1);
        }
        URL obj;
        try {
            obj = new URL(newUrl.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            return sendGet(con);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpAnswer sendGet(HttpURLConnection con) {
        try{
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            ByteArrayOutputStream os = new ByteArrayOutputStream(con.getContentLength());
            int responseCode = con.getResponseCode();
            InputStream in = con.getInputStream();

            byte[] bytes = new byte[2048];
            int length;
            while ((length = in.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            in.close();

            return new HttpAnswer(responseCode, os.toByteArray());
        }catch (IOException e){
            return new HttpAnswer(400, null);
        }
    }

    public static HttpAnswer sendGetPing(String url) {
        URL obj;
        
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {   
                response.append(inputLine);
            }
            in.close();

            return new HttpAnswer(200, response.toString().getBytes());
        } catch (IOException e) {
            return null;
        }
        
    }
}
