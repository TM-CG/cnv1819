package pt.ulisboa.tecnico.cnv.aws;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;


public class LoadBalancer {

  private static String AS_ID = "123";
  private static String LB_ID = "234";

  static AmazonEC2 ec2;
  static Set<Instance> instances;
  static Instance autoscaler;

  public static void init() {
    AWSCredentials credentials = null;
    try{
      credentials = new ProfileCredentialsProvider().getCredentials();
    }catch(Exception e){
      throw new AmazonClientException(
        "Cannot load the credentials from the credential profiles file. "
        + "Please make sure that your credentials file is at the correct "
        + "location (~/.aws/credentials), and is in valid format.", e);
    }
    ec2 = AmazonEC2ClientBuilder.standard().withRegion("eu-central-1")
    .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    recheckAWS();
  }

  public static void main(String[] args){
    init();
    
  }


  public static String selectWorker(){
    /*Sends the request to a worker instance*/
    requestCost();
    return "ugabuga";
  }

  public static void requestCost(){}

  public static void recheckAWS(){

    DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
    List<Reservation> reservations = describeInstancesRequest.getReservations();
    instances = new HashSet<Instance>();

    for (Reservation reservation : reservations) {
        instances.addAll(reservation.getInstances());
    }
    for (Instance instance : instances) {
      if (instance.getImageId().equals(LoadBalancer.AS_ID)){
        autoscaler = instance;
        instances.remove(instance);
      }
      if (instance.getImageId().equals(LoadBalancer.LB_ID)){
        instances.remove(instance);
      }
    }
  }

}
