package pt.ulisboa.tecnico.cnv.util;

import java.io.*;
import pt.ulisboa.tecnico.cnv.util.Metrics.BranchType;

public class LogReader {
    public static void main(String argv[]) {
        String fileName = argv[0];
        System.out.println("Reading " + fileName + "...");
        try {
            File file = new File(fileName);
            if(!file.exists()){
                System.out.println("File " + fileName + " does not exists");
            }
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);
            Metrics metrics = (Metrics) oi.readObject();
            System.out.println("Number of instructions: " + metrics.instructionsRun());
            System.out.println("Number of basic blocks: " + metrics.basicBlocks());
            System.out.println("Number of methods: " + metrics.methodsCalled());
	        System.out.println("Number of branches: " + metrics.branches());
	        System.out.println("Number of branches TAKEN: " + metrics.branches(BranchType.TAKEN));
            System.out.println("Number of branches NOT TAKEN: " + metrics.branches(BranchType.NOT_TAKEN));
            for(String s : metrics.getParams()){
                System.out.println(s);
            }        

            oi.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            e.printStackTrace();
        }
    }
}
