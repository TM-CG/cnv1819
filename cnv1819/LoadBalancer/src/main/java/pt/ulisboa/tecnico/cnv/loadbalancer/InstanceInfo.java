package pt.ulisboa.tecnico.cnv.loadbalancer;

import com.amazonaws.services.ec2.model.Instance;

import java.util.Date;
import java.util.List;


public class InstanceInfo {
    private Instance instance;
    private Date launchTime;
    private boolean setForDelete;
    private List<Job> jobs;

    public InstanceInfo(Instance instance) {
        this.instance = instance;
        this.launchTime = instance.getLaunchTime();
        this.setForDelete = false;
    }

    public String getInstanceId() {
        return instance.getInstanceId();
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public void setToDelete() {
        //guarantee that it does not receive extra jobs
        this.setForDelete = true;
    }

    public List<Job> getJobs() {
        return jobs;
    }

}
