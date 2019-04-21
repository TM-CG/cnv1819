import java.io.Serializable;

public class Metrics implements Serializable{

    static final long serialVersionUID = 1;
    private int methodsCalled;
    private int basicBlocks;
    private int instructionsRunned;

    public Metrics(){
        methodsCalled = 0;
        basicBlocks = 0;
        instructionsRunned = 0;
    }

    public void incMethods(){
        methodsCalled++;
    }

    public void incBasicBlocks(){
        basicBlocks++;
    }

    public void incInstructionsRunned(int instrunctions){
        instructionsRunned++;
    }

    public int methodsCalled(){
        return methodsCalled;
    }

    public int basicBlocks(){
        return basicBlocks;
    }

    public int instructionsRunned(){
        return instructionsRunned;
    }
}