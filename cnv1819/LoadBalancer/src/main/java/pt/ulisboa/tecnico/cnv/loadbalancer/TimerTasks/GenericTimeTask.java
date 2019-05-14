package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;

import java.util.Timer;
import java.util.TimerTask;

public abstract class GenericTimeTask extends TimerTask {

    protected Timer timer;
    protected LoadBalancer loadBalancer;

    public GenericTimeTask(LoadBalancer loadBalancer, int seconds) {
        this.loadBalancer = loadBalancer;
        this.timer = new Timer();
        timer.schedule(this, 0, 1000 * seconds);
    }

}
