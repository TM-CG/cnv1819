package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.MAX_INSTANCES;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;

public class AutoScaleVerifier extends GenericTimeTask {

    private InstanceManager instanceManager;

    private int upCounter;
    private int downCounter;

    public AutoScaleVerifier(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds, 30);
        this.instanceManager = instanceManager;
        upCounter = 0;
        downCounter = 0;
    }

    @Override
    public void run() {
        int numberOfInstances = loadBalancer.getInstanceSet().size();
        double sum = 0.0;
        List<Instance> instances;

        if (numberOfInstances == 0) {
            instances = instanceManager.launchInstance(1);
            /*for (Instance instance : instances) 
                loadBalancer.instanceInfoMap.put(instance.getPublicIpAddress(), new InstanceInfo(instance));*/
        }
        else if (numberOfInstances > 0 && numberOfInstances < MAX_INSTANCES){
            for(Map.Entry<String, InstanceInfo> entry : loadBalancer.getInstanceSet()){
                sum+= entry.getValue().getTotalCost();
            }
            double average = sum / numberOfInstances;

            if (average > 600000) {
                downCounter = 0;
                upCounter++;
                if(upCounter >= 3){
                    synchronized(loadBalancer.toDeleteLock) {
                        if(loadBalancer.toDelete.size() > 0){
                            loadBalancer.toDelete.get(0).setToDelete(false);
                        }
                        else{
                            instances = instanceManager.launchInstance(1);
                            System.out.println("Instances: " + instances.size());
                            /*for (Instance instance : instances) {
                                loadBalancer.instanceInfoMap.put(instance.getPublicIpAddress(), new InstanceInfo(instance));           
                                System.out.println("Adicionei ao hashmap");
                            }*/
                        }
                    }
                    upCounter = 0;
                }
               
            }
            else if (average <= 400000) { 
              upCounter = 0;
              downCounter++;
              if(downCounter >= 5) {
                  loadBalancer.setInstanceForDelete();

                  downCounter = 0;
              }
            }
          
        }
    }
}
