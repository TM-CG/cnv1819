import BIT.highBIT.*;
import java.io.*;
import java.util.*;


public class CNVMetric {
    private static PrintStream out = null;
    private static double i_count = 0, b_count = 0, m_count = 0, cond_count = 0;
    private static double arit_count = 0;
    private static double load_count = 0, store_count = 0, stack_count = 0;
    
    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        File file_in = new File(argv[0]);
        String infilenames[] = file_in.list();
        
        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
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
                        int opcode=instr.getOpcode();

                        if ((opcode==InstructionTable.CONDITIONAL_INSTRUCTION) || 
                            (opcode==InstructionTable.ARITHMETIC_INSTRUCTION) ||
                            (opcode==InstructionTable.LOAD_INSTRUCTION) ||
                            (opcode==InstructionTable.STORE_INSTRUCTION) ||
                            (opcode==InstructionTable.STACK_INSTRUCTION)
                            ){
                            instr.addBefore("CNVMetric", "dynamicCount", new Integer(opcode));
                        }
                    }
                    

                }
                ci.addAfter("CNVMetric", "printStats", ci.getClassName());
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }
    
    public static synchronized void printStats(String foo) {
        System.out.println("=============== RESULTS ===============");
        System.out.printf("%1.0f instructions in %1.0f basic blocks were executed in %1.0f methods\n", i_count, b_count, m_count);
        System.out.printf("%1.0f conditional instructions\n", cond_count);
        System.out.printf("%1.0f aritmetic instructions\n", arit_count);
        System.out.printf("%1.0f load instructions\n", load_count);
        System.out.printf("%1.0f store instructions\n", store_count);
        System.out.printf("%1.0f stack instructions\n", stack_count);
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
            case InstructionTable.CONDITIONAL_INSTRUCTION: cond_count++; break;
            case InstructionTable.ARITHMETIC_INSTRUCTION : arit_count++; break;
            case InstructionTable.LOAD_INSTRUCTION       : load_count++; break;
            case InstructionTable.STORE_INSTRUCTION      : store_count++; break;
            case InstructionTable.STACK_INSTRUCTION      : stack_count++; break;
        }
    }
}

