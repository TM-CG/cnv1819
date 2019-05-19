package pt.ulisboa.tecnico.cnv.loadbalancer;

import com.amazonaws.services.ec2.model.Instance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class InstanceInfo {

    private final Object jobLocker = new Object();
    private int jobCounter = 0;

    private long load;

    private Instance instance;
    private Date launchTime;
    private boolean setForDelete;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    private double totalCost;
    private ConcurrentHashMap<Integer, Job> jobs;

    public InstanceInfo(Instance instance) {
        this.instance = instance;
        this.launchTime = instance.getLaunchTime();
        this.setForDelete = false;
        this.load = 0;
        this.jobs = new ConcurrentHashMap<>();
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
            number = jobCounter++;
            jobs.put(number, job);
            totalCost += job.getCost();
        }
        return number;
    }

    public void removeJob(int i) {
        synchronized (jobLocker) {
            totalCost -= jobs.get(i).getCost();
            jobs.remove(i);
            jobCounter = jobCounter--;

        }
    }

    public Map<Integer, Job> getJobs() {
        return jobs;
    }

    public Instance getInstance() {
        return instance;
    }
}
