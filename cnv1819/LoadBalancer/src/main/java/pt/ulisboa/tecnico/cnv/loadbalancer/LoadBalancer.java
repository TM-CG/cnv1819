package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;


public class LoadBalancer extends TimerTask {

    private String MSS_IP = "35.22.232.232";
    private String MSS_PORT = "8000";

    private AmazonEC2 ec2;
    private AmazonCloudWatch cloudWatch;
    private InstanceManager instanceManager;
    private Map<String, InstanceInfo> instanceInfoMap;
    private Timer timer;

    public LoadBalancer(AmazonEC2 ec2, AmazonCloudWatch cloudWatch) {
        this.ec2 = ec2;
        this.cloudWatch = cloudWatch;
        instanceManager = new InstanceManager(this.ec2);
        instanceInfoMap = createInstanceMap();
    }

    private Map<String, InstanceInfo> createInstanceMap() {
        List<Instance> instances = instanceManager.listWorkerInstances();
        HashMap<String, InstanceInfo> infoHashMap = new HashMap<>();

        for (Instance instance : instances) {
           //instanceInfoMap.put(instance.getInstanceId(), new InstanceInfo(instance, cloudWatch.getMetricData()))
        }
        return infoHashMap;
    }

    protected List<String> listWorkers() {
        List<Instance> instanceList = instanceManager.listWorkerInstances();
        List<String> instancesIps = new ArrayList<>();

        for (Instance instance : instanceList) {
            instancesIps.add(instance.getPublicIpAddress());
        }

        return instancesIps;
    }

    public void getCloudWatchMetrics() {
        ListMetricsResult response = cloudWatch.listMetrics();
        List<Metric> metrics = response.getMetrics();

        for (Metric metric : metrics) {
            System.out.println(metric.getMetricName());
        }
    }


    public void requestMetricMss(Map<String, String> arguments) {
        HttpAnswer answer = HttpRequest.sendHttpRequest("http://" + MSS_IP + ":" + MSS_PORT + "/requestmetric", arguments);
        String response = answer.getString();
        System.out.println(response);
        Metrics metrics = Metrics.parseFromURL(response);
        System.out.println(metrics.getAlgorithm());
    }

    @Override
    public void run(){
    }

}
