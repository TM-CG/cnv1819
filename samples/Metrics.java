public class Metrics{
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

    public void incInstructionsRunned(){
        instructionsRunned++;
    }

    public int MethodsCalled(){
        return methodsCalled;
    }

    public int BasicBlocks(){
        return basicBlocks;
    }

    public int InstructionsRunned(){
        return instructionsRunned;
    }
}