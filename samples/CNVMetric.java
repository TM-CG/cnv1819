import BIT.highBIT.*;
import java.io.*;
import java.util.*;


public class CNVMetric {
    private static PrintStream out = null;
    private static double i_count = 0, b_count = 0, m_count = 0, unCond_count = 0;
    private static double arit_count = 0;
    private static double cond_count = 0, store_count = 0, stack_count = 0;
    
    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        File file_in = new File(argv[0]);
        String infilenames[] = file_in.list();
        
        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            System.out.println("Found file: " + infilename);
	    if (infilename.endsWith(".class")) {
		// create class info object
		ClassInfo ci = new ClassInfo(argv[0] + System.getProperty("file.separator") + infilename);
				
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
                	Routine routine = (Routine) e.nextElement();

                    	/** Add reference to mcount before all methods */
			            routine.addBefore("CNVMetric", "mcount", new Integer(1));

                    	for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                        	BasicBlock bb = (BasicBlock) b.nextElement();

                        	/** Count how many basic blocks exists */
                        	bb.addBefore("CNVMetric", "count", new Integer(bb.size()));
                   	 }

                    	//Check how many conditional jumps exists
                    	InstructionArray instructions = routine.getInstructionArray();

                    	for (Enumeration instrs = instructions.elements(); instrs.hasMoreElements(); ) {
                        	Instruction instr = (Instruction) instrs.nextElement();
                        	int opcode = instr.getOpcode();

                            if ((opcode == InstructionTable.UNCONDITIONAL_INSTRUCTION) ||
                                (opcode == InstructionTable.CONDITIONAL_INSTRUCTION)
                            		){
                            		instr.addBefore("CNVMetric", "dynamicCount", new Integer(opcode));
                        }
                    }
			
			//vitor: only print the statistics after solve function finish
			if (routine.getMethodName().equals("solve")) {
				routine.addAfter("CNVMetric", "printStats", "bah");
			}

                }
                /*ci.addAfter("CNVMetric", "printStats", ci.getClassName());*/
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }
    
    public static synchronized void printStats(String foo) {
        System.out.println("=============== RESULTS ===============");
        System.out.printf("%1.0f instructions\n", i_count);
        System.out.printf("%1.0f basic blocks\n", b_count);
        System.out.printf("%1.0f methods called\n", m_count);
        System.out.printf("%1.0f conditional instructions (IFs)\n", cond_count);
        System.out.printf("%1.0f unconditional instructions\n", unCond_count);
        /*System.out.printf("%1.0f conditional instructions (IFs)\n", cond_count);
        System.out.printf("%1.0f compararison instructions\n", comp_count);
        System.out.printf("%1.0f aritmetic instructions\n", arit_count);
        System.out.printf("%1.0f store instructions\n", store_count);
        System.out.printf("%1.0f stack instructions\n", stack_count);*/
        String fileName = Thread.currentThread().getId() + "-" + new Date().getTime();

        System.out.println("Writing " + fileName + "...");
        try {
            File file = new File("Logs"+File.separator + fileName + ".bin");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(i_count);
            o.writeObject(b_count);
            o.writeObject(m_count);
            o.writeObject(unCond_count);
            /*o.writeObject(cond_count);
            o.writeObject(comp_count);
            o.writeObject(arit_count);
            o.writeObject(store_count);
            o.writeObject(stack_count);*/

            o.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }
        System.out.println(fileName);
    }
    

    public static synchronized void count(int incr) {
        i_count += incr;
        b_count++;
    }

    public static synchronized void mcount(int incr) {
		m_count++;
    }

    public static synchronized void dynamicCount(int type) {
        switch(type) {
            case InstructionTable.UNCONDITIONAL_INSTRUCTION: unCond_count++;   break;
            case InstructionTable.CONDITIONAL_INSTRUCTION:   cond_count++;   break;
        }
    }
}

