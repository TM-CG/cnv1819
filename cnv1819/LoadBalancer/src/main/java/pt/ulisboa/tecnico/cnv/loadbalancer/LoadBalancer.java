package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;

import java.util.ArrayList;
import java.util.List;


public class LoadBalancer {


  AmazonEC2 ec2;
  InstanceManager instanceManager;

  public LoadBalancer(AmazonEC2 ec2){
    this.ec2 = ec2;
    instanceManager = new InstanceManager(ec2);
  }

  protected List<String> listWorkers() {
    List<Instance> instanceList = instanceManager.listWorkerInstances();
    List<String> instancesIps = new ArrayList<>();

    for (Instance instance : instanceList) {
      instancesIps.add(instance.getPublicIpAddress());
    }

    return instancesIps;
  }

}
