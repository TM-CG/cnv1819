package pt.ulisboa.tecnico.cnv.metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;


public class MetricHolder {
  
  public static ConcurrentHashMap<Long, Metrics>  metricsMap = new ConcurrentHashMap<>();

  public MetricHolder(){}

  public static synchronized void saveMetrics() {
    Metrics metrics = MetricHolder.metricsMap.get(Thread.currentThread().getId());
    try {
        File file = new File("/home/ec2-user/cnv-project/Logs" + File.separator + 1 + ".bin");
        file.createNewFile();
        FileOutputStream f = new FileOutputStream(file, false);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(metrics);
        o.close();
        MetricHolder.metricsMap.remove(Thread.currentThread().getId());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error initializing stream");
    }

  }
}