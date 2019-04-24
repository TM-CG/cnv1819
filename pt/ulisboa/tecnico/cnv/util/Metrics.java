package pt.ulisboa.tecnico.cnv.util;

import java.io.Serializable;

public class Metrics implements Serializable{

    static final long serialVersionUID = 3;
    public enum BranchType {TAKEN, NOT_TAKEN}

    private long basicBlocks;
    private long branches_notTaken;
    private int width;
    private int height;
    private int x0;
    private int y0;
    private int x1;
    private int y1;
    private int xS;
    private int yS;
    private String search;
    private String map;

    private String params[];

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
    }

    public void incBasicBlocks(){
        basicBlocks++;
    }

    public void incBranches(BranchType type) {
	if (type == BranchType.TAKEN)
		branches_taken++;
	else if (type == BranchType.NOT_TAKEN)
		branches_notTaken++;
    }

    public long basicBlocks(){
        return basicBlocks;
    }

    public long branches(BranchType type) {
	if (type == BranchType.TAKEN)
		return branches_taken;
	else if (type == BranchType.NOT_TAKEN)
		return branches_notTaken;

	return -1;
    }

    public void insertParams(String[] params){
        this.params = params;
    }

    public String[] getParams(){
        return this.params;
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
}
