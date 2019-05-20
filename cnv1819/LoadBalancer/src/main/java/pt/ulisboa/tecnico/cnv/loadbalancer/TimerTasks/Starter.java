package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.MalformedURLException;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpAnswer;
import pt.ulisboa.tecnico.cnv.HTTPLib.HttpRequest;

import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Instance;

public class Starter extends GenericTimeTask {

    private InstanceManager instanceManager;

    public Starter(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds);
        this.instanceManager = instanceManager;

    }

    @Override
    public void run() {
        List<Instance> instances = instanceManager.listWorkerInstances();
        List<InstanceInfo> tmpInstances = new ArrayList<>();

        for (Instance instance : instances) {
            if (!loadBalancer.instanceInfoMap.containsKey(instance.getPublicIpAddress()) && (!loadBalancer.toStart.containsKey(instance.getPublicIpAddress()))) {
                System.out.println("STARTER ADD INSTANCE: " + instance.getPublicIpAddress());
                loadBalancer.toStart.put(instance.getPublicIpAddress(), new InstanceInfo(instance));
            }
        }
        for (Map.Entry<String, InstanceInfo> entry : loadBalancer.toStart.entrySet()){
            HttpAnswer answer = HttpRequest.sendGetPing("http://" + entry.getKey() + ":8000/ping");


            if (answer != null) {
                String response = new String(answer.getResponse());
                if(response.equals("Pong!")){
                    tmpInstances.add(entry.getValue());
                    
                }
            }
            else {
                System.out.printf("Worker at %s not yet ready!", entry.getKey());
            }
           
        }

        for (InstanceInfo instanceInfo : tmpInstances) {
            loadBalancer.instanceInfoMap.put(instanceInfo.getInstance().getPublicIpAddress(), instanceInfo);
            loadBalancer.toStart.remove(instanceInfo.getInstance().getPublicIpAddress());
        }
    }
}