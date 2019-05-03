package pt.ulisboa.tecnico.cnv.metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;


public class MetricHolder {
  
  public static ConcurrentHashMap<Long, Metrics>  metricsMap = new ConcurrentHashMap<>();
  private static final String TBL_NAME = "metrics";

  public MetricHolder(){}

  // public void initAmazon(){
  //   System.out.println("AmazonDynamoDB is starting ...");
	// 	AmazonDynamoDBHelper.init();
	// 	System.out.println("AmazonDynamoDB: Ready!");
	// 	System.out.println("AmazonDynamoDB: Creating table metrics. Please wait ...");
  //       //create table if not exists
	// 	AmazonDynamoDBHelper.createTable(TBL_NAME);
	// 	System.out.println("AmazonDynamoDB: Table created!");
  // }
  
  public static synchronized void saveMetrics() {
    Metrics metrics = MetricHolder.metricsMap.get(Thread.currentThread().getId());
    try {
        //Store on dynamoDB
        // AmazonDynamoDBHelper.addMetricObject("metrics", Thread.currentThread().getId(), metrics);
        File file = new File("Logs" + File.separator + 1 + ".bin");
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