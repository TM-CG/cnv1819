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

    private double actualPercentage;
    
    Instance instance;

    private int id;

    public Job(Instance instance, double cost) {
        this.instance = instance;
        this.cost = cost;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getActualPercentage() {
        return this.actualPercentage;
    }

    public void setActualPercentage(double percentage) {
        this.actualPercentage = percentage;
    }

}
