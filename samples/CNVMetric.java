import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;


public class CNVMetric {
    private static HashMap<Long, Metrics>  metricsMap = new HashMap<>();
    private static int sequenceID = 0;

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
                    
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements();) {
                	Routine routine = (Routine) e.nextElement();
                    routine.addBefore("CNVMetric", "countMethod", new Integer(1));

                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements();) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("CNVMetric", "countInstBB", new Integer(bb.size()));
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

