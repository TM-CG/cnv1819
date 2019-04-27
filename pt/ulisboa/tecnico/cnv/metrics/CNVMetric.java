package pt.ulisboa.tecnico.cnv.metrics;

import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;
import pt.ulisboa.tecnico.cnv.metrics.AmazonDynamoDBHelper;
import pt.ulisboa.tecnico.cnv.metrics.Runner;

public class CNVMetric {
    private static int sequenceID = 0;

    AmazonDynamoDBHelper dynamoDB;
    /*
     * main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) throws Exception {

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

                    InstructionArray instructions = routine.getInstructionArray();
                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements();) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("CNVMetric", "countInstBB", new Integer(1));

                        Instruction instr = (Instruction) instructions.elementAt(bb.getEndAddress());
                        short instr_type = InstructionTable.InstructionTypeTable[instr.getOpcode()];
                        /* Branch taken and not taken counts */
                        if (instr_type == InstructionTable.CONDITIONAL_INSTRUCTION) {
                            instr.addBefore("CNVMetric", "countBranchOutcome", "BranchOutcome");
                            branches++;
                        }
                    }

                    if (routine.getMethodName().equals("solve")) {
                        routine.addAfter("CNVMetric", "saveMetric", "null");
                    }
                }
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static void countInstBB(int instructions) {
        Metrics metrics = Runner.metricsMap.get(Thread.currentThread().getId());
        if (metrics == null) {
            metrics = new Metrics();
            Runner.metricsMap.put(Thread.currentThread().getId(), metrics);
        }
        metrics.incBasicBlocks();
    }

    public static void countBranchOutcome(int br_outcome) {
        Metrics metrics = Runner.metricsMap.get(Thread.currentThread().getId());
        if (metrics == null) {
            metrics = new Metrics();
            Runner.metricsMap.put(Thread.currentThread().getId(), metrics);
        }
        /* Increment the global counter of branches */
        if (br_outcome == 0)
            metrics.incBranches();
    }

    public static synchronized void saveMetric(String foo) {
        Metrics metrics = Runner.metricsMap.get(Thread.currentThread().getId());
        try {
            //Store on dynamoDB
            // AmazonDynamoDBHelper.addMetricObject("metrics", Thread.currentThread().getId(), metrics);
            File file = new File("Logs" + File.separator + sequenceID + ".bin");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(metrics);
            o.close();
            sequenceID++;
            Runner.metricsMap.remove(Thread.currentThread().getId());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }

    }
}
