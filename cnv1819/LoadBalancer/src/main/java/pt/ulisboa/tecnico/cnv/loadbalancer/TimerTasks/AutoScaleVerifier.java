package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;
import static pt.ulisboa.tecnico.cnv.common.StaticConsts.MAX_INSTANCES;
import java.lang.Math;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;

public class AutoScaleVerifier extends GenericTimeTask {

    private InstanceManager instanceManager;

    private int upCounter;
    private int downCounter;

    public AutoScaleVerifier(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds, 5);
        this.instanceManager = instanceManager;
        upCounter = 0;
        downCounter = 0;
    }

    @Override
    public void run() {
        int numberOfInstances = loadBalancer.getInstanceSet().size();
        double sum = 0.0;
        List<Instance> instances;

        System.out.println("Checking");

        if (numberOfInstances == 0 && loadBalancer.toStart.size() == 0) {
            System.out.println("Houston: I'm about to launch!");
            instances = instanceManager.launchInstance(1);
            System.out.println("Houston: We have left off");
        }
        else if (numberOfInstances > 0  && loadBalancer.toStart.size() == 0){
            for(Map.Entry<String, InstanceInfo> entry : loadBalancer.getInstanceSet()){
                sum+= entry.getValue().getTotalCost();
            }

            double average;

            if (numberOfInstances > 1) {
                average = sum / (numberOfInstances - 0.5);
            } else {
                average = sum;
            }

            System.out.println("Average: " + average + "\nUpCounter: " + upCounter + "\nDownCounter: " + downCounter);

            if (average > 600000) {
                downCounter = 0;
                int adder = (int) (average/600000);
                upCounter += adder;
                if(upCounter >= 3){
                    synchronized(loadBalancer.toDeleteLock) {
                        if(loadBalancer.toDelete.size() > 0){
                            loadBalancer.toDelete.get(0).setToDelete(false);
                        }
                        else{
                            if(numberOfInstances < MAX_INSTANCES)
                                instances = instanceManager.launchInstance(1);
                          
                        }
                    }
                    upCounter = 0;
                }
               
            }
            else if (average <= 400000) { 
              upCounter = 0;
              if(numberOfInstances > 1){
                downCounter++;
                if(downCounter >= 5) {
                    loadBalancer.setInstanceForDelete();
  
                    downCounter = 0;
                } 
              }
            }
            else {
                upCounter = 0;
                downCounter = 0;
            }
          
        }
    }
}
