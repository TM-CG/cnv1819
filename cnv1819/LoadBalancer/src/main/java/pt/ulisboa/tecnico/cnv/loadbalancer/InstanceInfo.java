package pt.ulisboa.tecnico.cnv.loadbalancer;

import com.amazonaws.services.ec2.model.Instance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class InstanceInfo {

    private static final Object jobLocker = new Object();
    private static int jobCounter = 0;

    private long load;

    private Instance instance;
    private Date launchTime;
    private boolean setForDelete;
    private ConcurrentHashMap<Integer, Job> jobs;

    public InstanceInfo(Instance instance) {
        this.instance = instance;
        this.launchTime = instance.getLaunchTime();
        this.setForDelete = false;
        this.load = 0;
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

    public int addJob(Job job) {
        int number;
        synchronized (jobLocker) {
            number = InstanceInfo.jobCounter++;
            jobs.put(number, job);
        }
        return number;
    }

    public void removeJob(int i) {
        synchronized (jobLocker) {
            jobs.remove(i);
            InstanceInfo.jobCounter = InstanceInfo.jobCounter--;
        }
    }

    public Map<Integer, Job> getJobs() {
        return jobs;
    }

    public Instance getInstance() {
        return instance;
    }
}
