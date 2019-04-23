import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;
import pt.ulisboa.tecnico.cnv.util.Metrics;
import pt.ulisboa.tecnico.cnv.util.Metrics.BranchType;

public class CNVMetric {
    public static HashMap<Long, Metrics>  metricsMap = new HashMap<>();
    private static int sequenceID = 0;

    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        File file_in = new File(argv[0]);
        String infilenames[] = file_in.list();
        int branches = 0;


        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            System.out.println("Found file: " + infilename);
	        if (infilename.endsWith(".class")) {
                // create class info object
                ClassInfo ci = new ClassInfo(argv[0] + System.getProperty("file.separator") + infilename);   
                    
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements();) {
                    Routine routine = (Routine) e.nextElement();
                    routine.addBefore("CNVMetric", "countMethod", new Integer(1));
		    
                    InstructionArray instructions = routine.getInstructionArray();
		    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements();) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("CNVMetric", "countInstBB", new Integer(bb.size()));

			Instruction instr = (Instruction) instructions.elementAt(bb.getEndAddress());
			short instr_type = InstructionTable.InstructionTypeTable[instr.getOpcode()];
			/* Branch taken and not taken counts */
			if (instr_type == InstructionTable.CONDITIONAL_INSTRUCTION) {
				instr.addBefore("CNVMetric", "countBranchOutcome", "BranchOutcome");
				branches++;
			}
                    }

                    if(routine.getMethodName().equals("solve")){
                        routine.addAfter("CNVMetric", "saveMetric", "null");
                    }  
                }
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static synchronized void countInstBB(int instructions) {
        Metrics metrics =  metricsMap.get(Thread.currentThread().getId());
        if(metrics == null){
            metrics = new Metrics();
            metricsMap.put(Thread.currentThread().getId(), metrics);   
        }
        metrics.incInstructionsRun(instructions);
        metrics.incBasicBlocks();
    }

    public static synchronized void countMethod(int i) {
        Metrics metrics =  metricsMap.get(Thread.currentThread().getId());
        if(metrics == null){
            metrics = new Metrics();
            metricsMap.put(Thread.currentThread().getId(), metrics);   
        }
        metrics.incMethods();
    }

    public static synchronized void countBranchOutcome(int br_outcome) {
	Metrics metrics = metricsMap.get(Thread.currentThread().getId());
	if (metrics == null){
		metrics = new Metrics();
		metricsMap.put(Thread.currentThread().getId(), metrics);
	}

	/* Increment the global counter of branches */
	metrics.incBranches();

	if (br_outcome == 0)
		metrics.incBranches(BranchType.NOT_TAKEN);
	else	
		metrics.incBranches(BranchType.TAKEN);
    }

    public static synchronized void saveMetric(String foo) {
        Metrics metrics = metricsMap.get(Thread.currentThread().getId());
        try{
            File file = new File("Logs" + File.separator + sequenceID + ".bin");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(metrics);
            o.close();
            sequenceID++;
        }catch (IOException e ){
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }

    }
}

