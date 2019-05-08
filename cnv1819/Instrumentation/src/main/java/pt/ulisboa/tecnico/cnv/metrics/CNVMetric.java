import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;
import pt.ulisboa.tecnico.cnv.metrics.MetricHolder;

public class CNVMetric {
    /*
     * main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) throws Exception {

        File file_in = new File(argv[0]);
        String infilenames[] = file_in.list();

        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            System.out.println("Found file: " + infilename);
            if (infilename.endsWith(".class")) {
                // create class info object
                ClassInfo ci = new ClassInfo(argv[0] + System.getProperty("file.separator") + infilename);

                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements();) {
                    Routine routine = (Routine) e.nextElement();

                    InstructionArray instructions = routine.getInstructionArray();
                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements();) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("CNVMetric", "countInstBB", new Integer(1));

                        Instruction instr = (Instruction) instructions.elementAt(bb.getEndAddress());
                        short instr_type = InstructionTable.InstructionTypeTable[instr.getOpcode()];
                        /* Branch taken and not taken counts */
                        if (instr_type == InstructionTable.CONDITIONAL_INSTRUCTION) {
                            instr.addBefore("CNVMetric", "countBranchOutcome", "BranchOutcome");
                        }
                    }
                }
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static void countInstBB(int instructions) {
        Metrics metrics = MetricHolder.metricsMap.get(Thread.currentThread().getId());
        if (metrics == null) {
            metrics = new Metrics();
            MetricHolder.metricsMap.put(Thread.currentThread().getId(), metrics);
        }
        metrics.incBasicBlocks();
    }

    public static void countBranchOutcome(int br_outcome) {
        Metrics metrics = MetricHolder.metricsMap.get(Thread.currentThread().getId());
        if (metrics == null) {
            metrics = new Metrics();
            MetricHolder.metricsMap.put(Thread.currentThread().getId(), metrics);
        }
        /* Increment the global counter of branches */
        if (br_outcome == 0)
            metrics.incBranches();
    }
}
