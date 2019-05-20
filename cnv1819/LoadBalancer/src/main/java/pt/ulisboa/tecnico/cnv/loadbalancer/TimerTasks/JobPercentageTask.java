package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;

public class JobPercentageTask extends GenericTimeTask {
    
    public JobPercentageTask(LoadBalancer loadBalancer, int seconds) {
        super(loadBalancer, seconds, 5);
    }

    
    @Override
    public void run() {
        loadBalancer.getJobProgress();
    }
}