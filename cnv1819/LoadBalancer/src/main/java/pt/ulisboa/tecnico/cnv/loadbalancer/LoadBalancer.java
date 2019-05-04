package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.AmazonEC2;


public class LoadBalancer {


  AmazonEC2 ec2;
  InstanceManager instanceManager;

  public LoadBalancer(AmazonEC2 ec2){
    this.ec2 = ec2;
    instanceManager = new InstanceManager(ec2);
  }


}
