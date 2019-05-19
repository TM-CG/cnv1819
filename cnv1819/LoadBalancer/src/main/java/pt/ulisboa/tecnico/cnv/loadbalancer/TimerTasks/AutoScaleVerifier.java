package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;

import java.util.List;
import java.util.Map;

public class AutoScaleVerifier extends GenericTimeTask {

    private InstanceManager instanceManager;

    public AutoScaleVerifier(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds);
        this.instanceManager = instanceManager;

    }

    @Override
    public void run() {

        int numberOfInstances = loadBalancer.getInstanceSet().size();
        Double sum = 0.0;
        for(Map.Entry<String, InstanceInfo> entry : loadBalancer.instanceInfoMap.entrySet()){
            sum+= entry.getValue().getCpuUtilization();
        }
        Double average = sum / numberOfInstances;

        if(average >= 60.0){
            instanceManager.launchInstance(1);
        }
        else if(average <= 40) {
            List<String> toDelete = loadBalancer.setInstanceForDelete();
            while(loadBalancer.instanceInfoMap.get(toDelete.get(0)).getJobs().size() > 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { e.printStackTrace();
                }
            }
            instanceManager.terminateInstances(loadBalancer.setInstanceForDelete());
        }

    }
}
