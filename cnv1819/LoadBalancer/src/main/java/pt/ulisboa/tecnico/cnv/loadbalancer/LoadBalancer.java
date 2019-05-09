package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;

import java.util.*;


public class LoadBalancer extends TimerTask {

    private AmazonEC2 ec2;
    private AmazonCloudWatch cloudWatch;
    private InstanceManager instanceManager;
    private Timer timer;

    public LoadBalancer(AmazonEC2 ec2, AmazonCloudWatch cloudWatch) {
        this.ec2 = ec2;
        this.cloudWatch = cloudWatch;
        instanceManager = new InstanceManager(this.ec2);
    }

    protected List<String> listWorkers() {
        List<Instance> instanceList = instanceManager.listWorkerInstances();
        List<String> instancesIps = new ArrayList<>();

        for (Instance instance : instanceList) {
            instancesIps.add(instance.getPublicIpAddress());
        }

        return instancesIps;
    }

    private void getCloudWatchMetrics() {
        ListMetricsResult response = cloudWatch.listMetrics();
        List<Metric> metrics = response.getMetrics();

        for (Metric metric : metrics) {
            System.out.println(metric.getMetricName());
        }
    }


    public void run(){

    }

}
