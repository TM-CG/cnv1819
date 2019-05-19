package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;

import java.util.Map;

public class TestTimer extends GenericTimeTask {

    public TestTimer(LoadBalancer loadBalancer, int seconds) {
        super(loadBalancer, seconds);
    }

    @Override
    public void run() {
        System.out.println("=== TEST TIMER ===");
        for (Map.Entry<String, InstanceInfo> entry : loadBalancer.instanceInfoMap.entrySet()) {
            InstanceInfo instance = entry.getValue();
            System.out.printf("IP: %s\n", instance.getInstance().getPublicIpAddress());
            System.out.printf("# Jobs: %s\n", instance.getNumberOfJobs());
            System.out.printf("Cost: %f\n", instance.getTotalCost());
        }
    }
}