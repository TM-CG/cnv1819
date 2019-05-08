package pt.ulisboa.tecnico.cnv.HTTPLib;

public class HttpAnswer {

    private String string;
    private byte[] response;
    private int responseCode;

    public HttpAnswer(int responseCode, byte[] response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public HttpAnswer(int responseCode, String response) {
        this.responseCode = responseCode;
        this.string = response;
    }

    public String getString() {
        return this.string;
    }

    public byte[] getResponse() {return this.response; }

    public int getResponseCode() {
        return this.responseCode;
    }
}
