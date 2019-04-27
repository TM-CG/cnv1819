package pt.ulisboa.tecnico.cnv.metrics;

import java.util.concurrent.ConcurrentHashMap;


public class Runner {
  
  public static ConcurrentHashMap<Long, Metrics>  metricsMap = new ConcurrentHashMap<>();
  private static final String TBL_NAME = "metrics";

  public Runner(){}

  // public void initAmazon(){
  //   System.out.println("AmazonDynamoDB is starting ...");
	// 	AmazonDynamoDBHelper.init();
	// 	System.out.println("AmazonDynamoDB: Ready!");
	// 	System.out.println("AmazonDynamoDB: Creating table metrics. Please wait ...");
  //       //create table if not exists
	// 	AmazonDynamoDBHelper.createTable(TBL_NAME);
	// 	System.out.println("AmazonDynamoDB: Table created!");
  // }

}