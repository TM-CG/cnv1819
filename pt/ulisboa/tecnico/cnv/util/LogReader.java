package pt.ulisboa.tecnico.cnv.util;

import java.io.*;

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
            System.out.println("Number of instrunctions: " + metrics.instructionsRunned());
            System.out.println("Number of basic blocks: " + metrics.basicBlocks());
            System.out.println("Number of methods: " + metrics.methodsCalled());
        

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