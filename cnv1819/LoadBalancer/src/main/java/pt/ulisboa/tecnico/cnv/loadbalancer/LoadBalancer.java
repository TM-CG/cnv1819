package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;
import pt.ulisboa.tecnico.cnv.common.Common;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.GetMetricsCloudWatch;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.AutoScaleVerifier;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.TestTimer;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.*;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.*;

public class LoadBalancer {



    protected static int jobCounter  = 0;

    private AmazonEC2 ec2;
    private AmazonCloudWatch cloudWatch;
    public InstanceManager instanceManager;
    public Map<String, InstanceInfo> instanceInfoMap;

    public ArrayList<InstanceInfo> toDelete = new ArrayList<>();
    private GetMetricsCloudWatch getMetricsCloudWatchTask;

    private AutoScaleVerifier autoScale;
    private TestTimer testTimer;

    public LoadBalancer(AmazonEC2 ec2, AmazonCloudWatch cloudWatch) {
        this.ec2 = ec2;
        this.cloudWatch = cloudWatch;
        this.instanceManager = new InstanceManager(this.ec2);
        this.instanceInfoMap = createInstanceMap();
        getMetricsCloudWatchTask = new GetMetricsCloudWatch(this, cloudWatch,30);
        this.autoScale = new AutoScaleVerifier(this, this.instanceManager, 60);
        this.testTimer = new TestTimer(this, 10);

    }

    private Map<String, InstanceInfo> createInstanceMap() {
        List<Instance> instances = instanceManager.listWorkerInstances();
        System.out.println("instances size: " + instances.size());
        HashMap<String, InstanceInfo> infoHashMap = new HashMap<>();

        for (Instance instance : instances) {
            System.out.println("LB ADD INSTANCE: " + instance.getPublicIpAddress());
            infoHashMap.put(instance.getPublicIpAddress(), new InstanceInfo(instance));
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

    public double requestMetricMss(Map<String, String> arguments) {
        HttpAnswer answer = HttpRequest.sendHttpRequest("http://" + MSS_IP + ":" + MSS_PORT + "/requestmetric", arguments);
        String metricString = new String(answer.getResponse());

        return Double.valueOf(metricString);
    }

    public Set<Map.Entry<String, InstanceInfo>> getInstanceSet() {
        return instanceInfoMap.entrySet();
    }

    public InstanceInfo whichWorker() {

        return getInstanceWithLeastCost();

    }

    public InstanceInfo setInstanceForDelete() {
        InstanceInfo toDelete = null;
        double cost = -1;
        for (Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
            if(toDelete == null || cost == -1 || entry.getValue().getTotalCost() < cost) {
                toDelete = entry.getValue();
                cost = entry.getValue().getTotalCost();
            }
        }
        toDelete.setToDelete();
        return toDelete;

    }

    public InstanceInfo getInstanceWithLeastCost() {
        double cost = -1;
        InstanceInfo instance = null;
        for(Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
            if((entry.getValue().getTotalCost() < cost || cost == -1) && (entry.getValue().isToDelete() == false)) {
                System.out.println("COST: " + entry.getValue().getTotalCost()  + " cost: " + cost);
                cost = entry.getValue().getTotalCost();
                instance = entry.getValue();
            }
        }
        return instance;
    }
}
