package pt.ulisboa.tecnico.cnv.util;

import java.io.Serializable;

public class Metrics implements Serializable{

    static final long serialVersionUID = 2;
    private long methodsCalled;
    private long basicBlocks;
    private long instructionsRun;

    public Metrics(){
        methodsCalled = 0;
        basicBlocks = 0;
        instructionsRun = 0;
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

    public long methodsCalled(){
        return methodsCalled;
    }

    public long basicBlocks(){
        return basicBlocks;
    }

    public long instructionsRun(){
        return instructionsRun;
    }
}