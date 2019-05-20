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

        if (numberOfInstances == 0) {
            System.out.println("Houston: I'm about to launch!");
            instances = instanceManager.launchInstance(1);
            System.out.println("Houston: We have left off");
        }
        else if (numberOfInstances > 0 && numberOfInstances < MAX_INSTANCES && loadBalancer.toStart.size() == 0){
            System.out.println("More than 0 instances");
            for(Map.Entry<String, InstanceInfo> entry : loadBalancer.getInstanceSet()){
                sum+= entry.getValue().getTotalCost();
            }
            double average = sum / numberOfInstances;
            System.out.println("average: " + average + "upcounter: " + upCounter + "downCounter: " + downCounter);

            if (average > 600000) {
                downCounter = 0;
                upCounter++;
                if(upCounter >= 3){
                    System.out.println("Quero o lock");
                    synchronized(loadBalancer.toDeleteLock) {
                        System.out.println("tenho o lock");
                        if(loadBalancer.toDelete.size() > 0){
                            loadBalancer.toDelete.get(0).setToDelete(false);
                        }
                        else{
                            instances = instanceManager.launchInstance(1);
                            System.out.println("Instances: " + instances.size());
                          
                        }
                    }
                    System.out.println("Larguei o lock");
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
          
        }
    }
}
