package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;

import java.util.List;
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

        System.out.println("instances size: " + instances.size());

        for (Instance instance : instances) {
            if (!loadBalancer.instanceInfoMap.containsKey(instance.getPublicIpAddress())) {
                System.out.println("STARTER ADD INSTANCE: " + instance.getPublicIpAddress());
                loadBalancer.instanceInfoMap.put(instance.getPublicIpAddress(), new InstanceInfo(instance));
            }
        }
    }
}