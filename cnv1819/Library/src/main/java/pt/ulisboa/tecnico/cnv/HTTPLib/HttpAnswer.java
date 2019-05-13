package pt.ulisboa.tecnico.cnv.HTTPLib;

public class HttpAnswer {

    private byte[] response;
    private int responseCode;

    public HttpAnswer(int responseCode, byte[] response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public byte[] getResponse() {return this.response; }

    public int getResponseCode() {
        return this.responseCode;
    }
}
