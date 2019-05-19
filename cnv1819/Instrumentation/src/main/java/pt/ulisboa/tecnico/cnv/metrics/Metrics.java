package pt.ulisboa.tecnico.cnv.metrics;

import java.io.Serializable;
/**
 * A class for describing Metrics
 */
public class Metrics implements Serializable{

    static final long serialVersionUID = 3;

    public long getBasicBlocks() {
        return basicBlocks;
    }

    public long getBranches_notTaken() {
        return branches_notTaken;
    }

    /** The number of basic blocks */
    private long basicBlocks;
    /** The number of branches not taken */
    private long branches_notTaken;

    /** Size of the map */
    private int width;
    private int height;

    /** Upper-left corner */
    private int x0;
    private int y0;

    /** Lower-right corner */
    private int x1;
    private int y1;

    /** Starting point */
    private int xS;
    private int yS;

    /** The search algorithm */
    private String search;

    /** The map image */
    private String map;

    private double cost;

    public Metrics(){
        basicBlocks = 0;
        branches_notTaken = 0;
        width = 0;
        height = 0;
        x0 = 0;
        y0 = 0;
        x1 = 0;
        y1 = 0;
        xS = 0;
        yS = 0;
        cost = 0;
    }

    public int getWidth() {
       return this.width;
    }

    public int getHeight() {
	return this.height;
    }

    public int getX0() {
	return this.x0;
    }

    public int getY0() {
	return this.y0;
    }

    public int getX1() {
	return this.x1;
    }

    public int getY1() {
	return this.y1;
    }

    public int getXS() {
	return this.xS;
    }

    public int getYS() {
	return this.yS;
    }

    public String getAlgorithm() {
	return this.search;
    }

    public String getMap() {
	return this.map;
    }

    public void incBasicBlocks(){
        basicBlocks++;
    }

    public void incBranches() {
	    branches_notTaken++;
    }

    public long basicBlocks(){
        return basicBlocks;
    }

    public long getBranches() {
		return branches_notTaken;
    }

    public void setBranches(long branches) {
        this.branches_notTaken = branches;
    }

    public void setBasicBlocks(long basicBlocks) {
        this.basicBlocks = basicBlocks;
    }

    public void insertArgs(int width, int height, int x0, int x1, int y0, int y1, int xS, int yS, String search, String name){
        this.width = width;
        this.height = height;
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.xS = xS;
        this.yS = yS;
        this.search = search;
        this.map = name;
    }

    /**
     * Given a url return a parsed Metrics object
     * @param url to be parsed
     * @return Metrics object
     */
    public static Metrics parseFromURL(String url) {
        Metrics metric = new Metrics();

        String[] params = url.split("&");
        
        for (String param: params) {
            String[] splited = param.split("=");
            
            String paramName = splited[0];
            String value = splited[1];

            metric.loadParams(paramName, value);
        }

        return metric;
    }

    private void loadParams(String param, String value) {
        switch (param) {
            case "w" : this.width  = Integer.parseInt(value); break;
            case "h" : this.height = Integer.parseInt(value); break;
            case "x0": this.x0     = Integer.parseInt(value); break;
            case "x1": this.x1     = Integer.parseInt(value); break;
            case "y0": this.y0     = Integer.parseInt(value); break;
            case "y1": this.y1     = Integer.parseInt(value); break;
            case "xS": this.xS     = Integer.parseInt(value); break;
            case "yS": this.yS     = Integer.parseInt(value); break;
            case "s" : this.search = value; break;
            case "i" : this.map    = value; break;
        }
    }

    @Override
    public String toString() {
        return "w=" + this.width + "&h=" + this.height + "&x0=" + this.x0 + "&x1=" + this.x1 + "&y0=" + this.y1
                + "&xS=" + this.xS + "&yS=" + this.yS + "&s=" + this.search + "&i=" + this.map + "&bb=" + this.basicBlocks + "&bnt=" + this.branches_notTaken +  "&" ;
    }

    public String toStringForId() {
        return "" + this.width + this.height + this.x0 + this.x1 + this.y1 + this.xS + this.yS + this.search + this.map;
    }

    public String toStringForCost() {
        return "w=" + this.width + "&h=" + this.height + "&x0=" + this.x0 + "&x1=" + this.x1 + "&y0=" + this.y1
        + "&xS=" + this.xS + "&yS=" + this.yS + "&s=" + this.search + "&i=" + this.map + "&bb=" + this.basicBlocks + "&bnt=" + this.branches_notTaken +  "&c=" + this.cost + 
        "&" ;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setXS(int xS) {
        this.xS = xS;
    }
    public void setYS(int yS) {
        this.yS = yS;
    }

    public void setAlgorithm(String search) {
        this.search = search;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
