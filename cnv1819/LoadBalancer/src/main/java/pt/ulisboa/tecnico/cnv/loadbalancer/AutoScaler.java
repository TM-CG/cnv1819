package pt.ulisboa.tecnico.cnv.loadbalancer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.auth.AWSStaticCredentialsProvider;


public class AutoScaler {

  static AmazonEC2 ec2;

  private static void init(){
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
  }

  public static void main(String[] args){
    init();
    getNumberOfInstances();
  }

  public static void launchInstance(){
    RunInstancesRequest runInstancesRequest =  new RunInstancesRequest();

    runInstancesRequest.withImageId("ami-0086b9668000c59ae")
                       .withInstanceType("t2.micro")
                       .withMinCount(1)
                       .withMaxCount(1)
                       .withKeyName("cnv-project")
                       .withSecurityGroups("cnv-project");

    RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
    String newInstanceId = runInstancesResult.getReservation().getInstances()
    .get(0).getInstanceId();
  }

  public static void terminateInstance(String instanceID){
    TerminateInstancesRequest termInstanceReq = new TerminateInstancesRequest();
    termInstanceReq.withInstanceIds(instanceID);
    ec2.terminateInstances(termInstanceReq);
  }

  public static void getNumberOfInstances(){
    DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
    List<Reservation> reservations = describeInstancesRequest.getReservations();
    Set<Instance> instances = new HashSet<Instance>();

    for (Reservation reservation : reservations) {
        instances.addAll(reservation.getInstances());
    }

    for (Instance instance : instances) {
      System.out.println(instance.getInstanceId());
    }

    System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");
  }
}


