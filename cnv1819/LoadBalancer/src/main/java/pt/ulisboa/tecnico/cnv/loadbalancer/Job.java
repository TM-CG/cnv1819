package pt.ulisboa.tecnico.cnv.loadbalancer;

import com.amazonaws.services.autoscaling.model.Instance;

public class Job {

    public enum State {
        RUNNING,
        COMPLETE,
        ERROR
    }

    Instance intance;


    public Job() {

    }
}
