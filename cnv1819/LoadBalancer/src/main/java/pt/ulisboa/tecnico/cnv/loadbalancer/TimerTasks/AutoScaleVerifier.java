package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.MAX_INSTANCES;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.Instance;

public class AutoScaleVerifier extends GenericTimeTask {

    private InstanceManager instanceManager;

    public AutoScaleVerifier(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds, 30);
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
        System.out.println("AutoScale check");
        if(average >= 60.0){
            System.out.println("Average >= 60");
            if (numberOfInstances < MAX_INSTANCES) {
                List<Instance> instances = instanceManager.launchInstance(1);
                for (Instance instance : instances) {
                    loadBalancer.instanceInfoMap.put(instance.getPublicIpAddress(), new InstanceInfo(instance));
                }
            }
        }
        else if(average <= 40) {
            
            InstanceInfo toDelete = loadBalancer.setInstanceForDelete();
            if (loadBalancer.instanceInfoMap.size() > 1) {
                if(toDelete != null ){
                    while(toDelete.isToDelete() && toDelete.getJobs().size() > 0){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    int stateCode;
                    List<InstanceStateChange> state;
                    do {
                        state = instanceManager.terminateInstances(loadBalancer.setInstanceForDelete());
                        stateCode = state.get(0).getCurrentState().getCode();
                        Thread.sleep(1000);
                    } while (stateCode != 32 && stateCode != 48);
                    
                    loadBalancer.instanceInfoMap.remove(toDelete.getInstance().getPublicIpAddress());
                }
            }
        }

    }
}
