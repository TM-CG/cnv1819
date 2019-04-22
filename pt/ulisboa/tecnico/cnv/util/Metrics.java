package pt.ulisboa.tecnico.cnv.util;

import java.io.Serializable;

public class Metrics implements Serializable{

    static final long serialVersionUID = 2;
    public enum BranchType {TAKEN, NOT_TAKEN}

    private long methodsCalled;
    private long basicBlocks;
    private long instructionsRun;    
    private long branches;
    private long branches_notTaken;
    private long branches_taken;

    public Metrics(){
        methodsCalled = 0;
        basicBlocks = 0;
        instructionsRun = 0;
        branches = 0;
        branches_notTaken = 0;
        branches_taken = 0;
    }

    public void incMethods(){
        methodsCalled++;
    }

    public void incBasicBlocks(){
        basicBlocks++;
    }

    public void incInstructionsRun(int instructions){
        instructionsRun+= instructions;
    }

    public void incBranches(BranchType type) {
	if (type == BranchType.TAKEN)
		branches_taken++;
	else if (type == BranchType.NOT_TAKEN)
		branches_notTaken++;
    }

    public void incBranches() {
	branches++;
    }

    public long methodsCalled(){
        return methodsCalled;
    }

    public long basicBlocks(){
        return basicBlocks;
    }

    public long instructionsRun(){
        return instructionsRun;
    }

    public long branches() {
	return branches;
    }

    public long branches(BranchType type) {
	if (type == BranchType.TAKEN)
		return branches_taken;
	else if (type == BranchType.NOT_TAKEN)
		return branches_notTaken;

	return -1;
    }
}
