package pt.ulisboa.tecnico.cnv.HTTPLib;

public class HttpAnswer {

    String response;
    int responseCode;

    public HttpAnswer(int responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}
