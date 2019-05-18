package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.model.Instance;

public class Job {

    public enum State {
        NEW,
        SEEN
    }

    private double percent;

    private State state;

    Instance instance;


    public Job(Instance instance, State state) {
        this.instance = instance;
        this.state = state;
    }
}
