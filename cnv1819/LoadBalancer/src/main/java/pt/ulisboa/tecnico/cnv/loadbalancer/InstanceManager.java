package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import pt.ulisboa.tecnico.cnv.common.StaticConsts;

import java.util.ArrayList;
import java.util.List;

public class InstanceManager {


    private final AmazonEC2 ec2;

    public InstanceManager(AmazonEC2 ec2){
        this.ec2 = ec2;
    }

    public void launchInstance(int numberOfInstances){

        if( numberOfInstances > 0) {
            RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

            runInstancesRequest.withImageId(StaticConsts.WORKER_AMI)
                    .withInstanceType(StaticConsts.INSTANCE_TYPE)
                    .withMinCount(numberOfInstances)
                    .withMaxCount(numberOfInstances)
                    .withKeyName(StaticConsts.KEY_NAME)
                    .withSecurityGroups(SECURITY_GROUP);

            RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
            System.out.println(runInstancesResult);
        }

    }

    public List<InstanceStateChange> terminateInstances(List<String> instancesIds){

        if(instancesIds.size() > 0) {
            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
            terminateInstancesRequest.setInstanceIds(instancesIds);

            TerminateInstancesResult result = ec2.terminateInstances(terminateInstancesRequest);
            return result.getTerminatingInstances();
        }
        return null;

    }

    protected List<Instance> listWorkerInstances(){
        List<Instance> workers = new ArrayList<>();
        List<Instance> instances = getInstancesFromReservation();

        if(instances != null){
            for (Instance instance : instances) {
                if(!instance.getImageId().equals(StaticConsts.WORKER_AMI))
                    continue;
                workers.add(instance);
            }
        }
        return workers;
    }

    protected Instance getMssInstance() {
        List<Instance> instances = getInstancesFromReservation();

        for (Instance instance : instances) {
            if(instance.getImageId().equals(StaticConsts.MSS_AMI))
                return instance;
        }
        return null;
    }

    private List<Instance> getInstancesFromReservation(){
        List<Instance> instances = new ArrayList<>();
        try{
            List<String> filter = new ArrayList<>();
            filter.add("running");
            Filter runningFilter = new Filter("instance-state-name", filter);

            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult describeInstancesResult = ec2.describeInstances(request.withFilters(runningFilter));
            List<Reservation> reservations = describeInstancesResult.getReservations();

            for (Reservation reservation : reservations) {
                instances.addAll(reservation.getInstances());
            }
        } catch (AmazonEC2Exception ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Response Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }
        return instances;
    }
}


