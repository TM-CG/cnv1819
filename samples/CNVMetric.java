import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;


public class CNVMetric {
    private static PrintStream out = null;
    private static HashMap<Long, Metrics>  metricsMap = new HashMap<>();

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
			            routine.addBefore("CNVMetric", "countMethod", new Integer(1));

                    	for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                        	BasicBlock bb = (BasicBlock) b.nextElement();

                        	/** Count how many basic blocks exists */
                        	bb.addBefore("CNVMetric", "countInstBB", new Integer(bb.size()));
                   	 }
                }
                /*ci.addAfter("CNVMetric", "printStats", ci.getClassName());*/
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static synchronized void countInstBB(int instructions) {
        Metrics metrics =  metricsMap.get(Thread.currentThread().getId());
        if(metrics == null){
            metricsMap.put(Thread.currentThread().getId(), new Metrics());
        }
        metrics.incInstructionsRunned(instructions);
        metrics.incBasicBlocks();
        
    }

    public static synchronized void countMethod(int i) {
        Metrics metrics =  metricsMap.get(Thread.currentThread().getId());
        if(metrics == null){
            metricsMap.put(Thread.currentThread().getId(), new Metrics());
        }
        metrics.incMethods();
    }
}

