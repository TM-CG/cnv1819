package pt.ulisboa.tecnico.cnv.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
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
    DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
    System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
    " Availability Zones.");
    }

  public void launchInstance(){
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

  public void terminateInstance(String instanceID){
    TerminateInstancesRequest termInstanceReq = new TerminateInstancesRequest();
    termInstanceReq.withInstanceIds(instanceID);
    ec2.terminateInstances(termInstanceReq);
  }

  public void getNumberOfInstances(){
    DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
    System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
    " Availability Zones.");
  }
}


