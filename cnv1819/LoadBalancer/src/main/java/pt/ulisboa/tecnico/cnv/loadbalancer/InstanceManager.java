package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class InstanceManager {

  private static final String WORKER_AMI_ID = "ami-123";
  private static final String INSTANCE_TYPE = "t2.micro";
  private static final String SECURITY_GROUP = "cnv-project";
  private static final String KEY_NAME = "cnv-project";

  private final AmazonEC2 ec2;

  public InstanceManager(AmazonEC2 ec2){
    this.ec2 = ec2;
  }

  private void launchInstance(int numberOfInstances){

    if( numberOfInstances > 0) {
      RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

      runInstancesRequest.withImageId(WORKER_AMI_ID)
              .withInstanceType(INSTANCE_TYPE)
              .withMinCount(numberOfInstances)
              .withMaxCount(numberOfInstances)
              .withKeyName(KEY_NAME)
              .withSecurityGroups(SECURITY_GROUP);

      RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
      System.out.println(runInstancesResult);
    }

  }

  private List<InstanceStateChange> terminateInstances(List<String> instancesIds){

    if(instancesIds.size() > 0) {
      TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
      terminateInstancesRequest.setInstanceIds(instancesIds);

      TerminateInstancesResult result = ec2.terminateInstances(terminateInstancesRequest);
      return result.getTerminatingInstances();
    }
    return null;

  }

  private List<Instance> listWorkerInstances(){
    List<Instance> workers = new ArrayList<>();

    List<String> filter = new ArrayList<>();
    filter.add("running");
    Filter runningFilter = new Filter("instance-state-name", filter);

    try{
      DescribeInstancesRequest request = new DescribeInstancesRequest();
      DescribeInstancesResult describeInstancesResult = ec2.describeInstances(request.withFilters(runningFilter));
      List<Reservation> reservations = describeInstancesResult.getReservations();

      List<Instance> instances = new ArrayList<>();
      for (Reservation reservation : reservations) {
        instances.addAll(reservation.getInstances());
      }
      for (Instance instance : instances) {
        if(!instance.getImageId().equals(WORKER_AMI_ID))
          continue;
        workers.add(instance);
      }

    } catch (AmazonEC2Exception ase) {
      System.out.println("Caught Exception: " + ase.getMessage());
      System.out.println("Reponse Status Code: " + ase.getStatusCode());
      System.out.println("Error Code: " + ase.getErrorCode());
      System.out.println("Request ID: " + ase.getRequestId());
    }

    return workers;
  }

}


