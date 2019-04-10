package pt.ulisboa.tecnico.cnv.util;

import java.io.*;

public class LogReader {
    public static void main(String argv[]) {
        String fileName = argv[0];
        System.out.println("Reading " + fileName + "...");
        try {
            File file = new File("Logs"+File.separator + fileName + ".bin");
            if(!file.exists()){
                System.out.println("File " + fileName + " does not exists");
            }
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);
            double i_count = (double) oi.readObject();
            double b_count = (double) oi.readObject();
            double m_count = (double) oi.readObject();
            System.out.printf("%1.0f instructions in %1.0f basic blocks were executed in %1.0f methods\n", i_count, b_count, m_count);
            double cond_count = (double) oi.readObject();
            System.out.printf("%1.0f conditional instructions\n", cond_count);
            double arit_count = (double) oi.readObject();
            System.out.printf("%1.0f aritmetic instructions\n", arit_count);
            double load_count = (double) oi.readObject();
            System.out.printf("%1.0f load instructions\n", load_count);
            double store_count = (double) oi.readObject();
            System.out.printf("%1.0f store instructions\n", store_count);
            double stack_count = (double) oi.readObject();
            System.out.printf("%1.0f stack instructions\n", stack_count);

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