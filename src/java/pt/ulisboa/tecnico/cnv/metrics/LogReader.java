package pt.ulisboa.tecnico.cnv.metrics;

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
	    System.out.printf("# Basic Blocks: %d\n", metrics.basicBlocks());
	    System.out.printf("# Branches NOT TAKEN: %d\n", metrics.getBranches());
	    System.out.printf("Map Size (Witdth x Height): (%d x %d)\n", metrics.getWidth(), metrics.getHeight());
	    System.out.printf("Upper-left corner: (%d , %d)\n", metrics.getX0(), metrics.getY0());
	    System.out.printf("Lower-right corner: (%d, %d)\n", metrics.getX1(), metrics.getY1());
	    System.out.printf("Starting point: (%d, %d)\n", metrics.getXS(), metrics.getYS());
            System.out.printf("Search Algorithm: %s\n", metrics.getAlgorithm());
	    System.out.printf("Path to map: %s\n", metrics.getMap());

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
