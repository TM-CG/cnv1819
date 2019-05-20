package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceManager;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;

import java.util.List;
import com.amazonaws.services.ec2.model.InstanceStateChange;

public class Terminator extends GenericTimeTask {

    private InstanceManager instanceManager;

    public Terminator(LoadBalancer loadBalancer, InstanceManager instanceManager, int seconds) {
        super(loadBalancer, seconds, 30);
        this.instanceManager = instanceManager;

    }

    @Override
    public void run() {
        List<InstanceStateChange> state;
        int stateCode;
        synchronized(loadBalancer.toDeleteLock) {
            for (InstanceInfo instance : loadBalancer.toDelete ) {
                if (instance.isToDelete() && instance.getJobs().size() == 0) {
                    //terminate instance
                    state = instanceManager.terminateInstances(loadBalancer.setInstanceForDelete());
                    stateCode = state.get(0).getCurrentState().getCode();

                    if ( stateCode == 32 || stateCode == 48) 
                        loadBalancer.instanceInfoMap.remove(instance.getInstance().getPublicIpAddress());
                        
                }
            }
        }
    }
}