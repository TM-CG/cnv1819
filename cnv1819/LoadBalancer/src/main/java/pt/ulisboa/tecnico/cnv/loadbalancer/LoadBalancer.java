package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;
import pt.ulisboa.tecnico.cnv.common.Common;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.GetMetricsCloudWatch;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.*;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.*;


public class LoadBalancer {



    protected static int jobCounter  = 0;

    private AmazonEC2 ec2;
    private AmazonCloudWatch cloudWatch;
    public InstanceManager instanceManager;
    public Map<String, InstanceInfo> instanceInfoMap;
    public Map<String, Double> cpuUtilization;

    public ArrayList<InstanceInfo> toDelete = new ArrayList<>();
    private GetMetricsCloudWatch getMetricsCloudWatchTask;

    public LoadBalancer(AmazonEC2 ec2, AmazonCloudWatch cloudWatch) {
        this.ec2 = ec2;
        this.cloudWatch = cloudWatch;
        this.instanceManager = new InstanceManager(this.ec2);
        this.instanceInfoMap = createInstanceMap();
        this.cpuUtilization = new HashMap<>();
        getMetricsCloudWatchTask = new GetMetricsCloudWatch(this, cloudWatch,30);

    }

    private Map<String, InstanceInfo> createInstanceMap() {
        List<Instance> instances = instanceManager.listWorkerInstances();
        HashMap<String, InstanceInfo> infoHashMap = new HashMap<>();

        for (Instance instance : instances) {
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
        System.out.println(metricString);

        Map<String, String> argumentsMap = Common.argumentsFromQuery(metricString);
        Metrics metric = Common.metricFromArguments(argumentsMap);

        return metric.getCost();
    }

    public Set<Map.Entry<String, InstanceInfo>> getInstanceSet() {
        return instanceInfoMap.entrySet();
    }

    public InstanceInfo whichWorker(String query) {
        double cost = requestMetricMss(Common.argumentsFromQuery(query));
        List<String> workers = this.listWorkers();

        return this.instanceInfoMap.get(workers.get(0));
    }

    public List<String> setInstanceForDelete() {
        InstanceInfo toDelete = null;for (Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
            if(toDelete == null || entry.getValue().getLaunchTime().before(toDelete.getLaunchTime()))
                toDelete = entry.getValue();
        }
        toDelete.setToDelete();
        ArrayList<String> toDeleteList = new ArrayList<>();
        toDeleteList.add(toDelete.getInstanceId());
        return toDeleteList;

    }

}
