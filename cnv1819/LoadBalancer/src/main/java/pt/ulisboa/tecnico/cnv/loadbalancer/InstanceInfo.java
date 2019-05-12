package pt.ulisboa.tecnico.cnv.loadbalancer;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.model.Instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InstanceInfo {
    private Instance instance;
    HashMap<String, Long> metrics;
    private Date launchTime;

    public InstanceInfo(Instance instance, List<Metric> metricList) {
        this.instance = instance;
        //add metrics to class
        this.launchTime = instance.getLaunchTime();
    }
}
