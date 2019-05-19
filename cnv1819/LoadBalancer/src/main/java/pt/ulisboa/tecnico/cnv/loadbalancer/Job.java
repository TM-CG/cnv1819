package pt.ulisboa.tecnico.cnv.loadbalancer;


import com.amazonaws.services.ec2.model.Instance;

public class Job {

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    private double cost;


    Instance instance;


    public Job(Instance instance, double cost) {
        this.instance = instance;
        this.cost = cost;
    }
}
