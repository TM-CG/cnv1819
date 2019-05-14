package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;

import java.util.*;


public class LoadBalancer extends TimerTask {

    private String MSS_IP = "35.156.23.222";
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
        timer = new Timer();
        timer.schedule(this, 0, 5000);
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
        long offsetInMilliseconds = 1000 * 60 * 60 * 24;
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withPeriod(60 * 60)
                .withEndTime(new Date())
                .withNamespace("AWS/EC2")
                .withDimensions(new Dimension().withName("ImageId").withValue("ami-09def150731bdbcc2"))
                .withMetricName("CPUUtilization")
                .withStatistics("Average", "Maximum");
        GetMetricStatisticsResult result = cloudWatch.getMetricStatistics(request);

        System.out.println("Label: " + result.getLabel());
    }


    public HttpAnswer requestMetricMss(Map<String, String> arguments) {
        return HttpRequest.sendHttpRequest("http://" + MSS_IP + ":" + MSS_PORT + "/requestmetric", arguments);
    }

    @Override
    public void run(){
        getCloudWatchMetrics();
    }

}
