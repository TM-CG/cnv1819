package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;
import pt.ulisboa.tecnico.cnv.common.StaticConsts;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.AutoScaleVerifier;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.TestTimer;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.Terminator;
import pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks.Starter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.*;

public class LoadBalancer {

    private AmazonEC2 ec2;
    public InstanceManager instanceManager;

    private Object lock = new Object();
    public ConcurrentHashMap<String, InstanceInfo> instanceInfoMap;

    public Object toDeleteLock = new Object();
    public ArrayList<InstanceInfo> toDelete = new ArrayList<>();
    public ConcurrentHashMap<String, InstanceInfo> toStart = new ConcurrentHashMap<>();

    private AutoScaleVerifier autoScale;
    private TestTimer testTimer;
    private Terminator terminator;
    private Starter starter;

    public LoadBalancer(AmazonEC2 ec2) {
        this.ec2 = ec2;
        this.instanceManager = new InstanceManager(this.ec2);
        this.instanceInfoMap = createInstanceMap();
        this.autoScale = new AutoScaleVerifier(this, this.instanceManager, 30);
        this.starter = new Starter(this, this.instanceManager, 10);
        this.terminator = new Terminator(this, this.instanceManager, 25);
        this.testTimer = new TestTimer(this, 10);

    }

    private ConcurrentHashMap<String, InstanceInfo> createInstanceMap() {
        List<Instance> instances = instanceManager.listWorkerInstances();
        System.out.println("instances size: " + instances.size());
        ConcurrentHashMap<String, InstanceInfo> infoHashMap = new ConcurrentHashMap<>();

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
        InstanceInfo instance = getInstanceWithLeastCost();
        while (instance == null) {
            instance = getInstanceWithLeastCost();
            try {
                Thread.sleep(5000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return instance;

    }

    public void getJobProgress() {

        for (Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
            HttpAnswer answer = HttpRequest.sendGetPing("http://" + entry.getValue().getInstance().getPublicIpAddress() + ":8000/progress");
            String response = new String(answer.getResponse());
            String[] lines = response.split("\n");
            for(String s : lines){
                String[] params  = s.split(" ");
                Job job = entry.getValue().getJobs().get(Integer.valueOf(params[0]));
                job.setActualPercentage(Double.valueOf(params[1]));
            }
        }
    }

    public void setInstanceForDelete() {
        InstanceInfo toDelete = null;
        double cost = -1;
        for (Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
            if(toDelete == null || cost == -1 || entry.getValue().getTotalCost() < cost) {
                toDelete = entry.getValue();
                cost = entry.getValue().getTotalCost();
            }
        }
        toDelete.setToDelete(true);
        synchronized(toDeleteLock){
            this.toDelete.add(toDelete);
        }
    }

    public InstanceInfo getInstanceWithLeastCost() {

        double cost = -1;
        InstanceInfo instance = null;
        synchronized(lock){
            for(Map.Entry<String, InstanceInfo> entry : instanceInfoMap.entrySet()) {
                if((entry.getValue().getTotalCost() < cost || cost == -1) && (entry.getValue().isToDelete() == false) &&
                entry.getValue().getTotalCost() < StaticConsts.MAX_COST) {
                    System.out.println("COST: " + entry.getValue().getTotalCost()  + " cost: " + cost);
                    cost = entry.getValue().getTotalCost();
                    instance = entry.getValue();
                }
            }
        }
       
        return instance;
    }
}
