package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import pt.ulisboa.tecnico.cnv.loadbalancer.InstanceInfo;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetMetricsCloudWatch extends GenericTimeTask {

    private AmazonCloudWatch cloudWatch;

    public GetMetricsCloudWatch(LoadBalancer loadBalancer, AmazonCloudWatch cloudWatch, int seconds) {
        super(loadBalancer, seconds);
        this.cloudWatch = cloudWatch;
    }

    private void getCloudWatchMetrics(InstanceInfo instance) {

        long offsetInMilliseconds = 1000 * 60;
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withPeriod(60)
                .withEndTime(new Date())
                .withNamespace("AWS/EC2")
                .withDimensions(new Dimension().withName("InstanceId").withValue(instance.getInstanceId()))
                .withMetricName("CPUUtilization")
                .withStatistics("Average", "Maximum");
        GetMetricStatisticsResult result = cloudWatch.getMetricStatistics(request);
        List<Datapoint> data = result.getDatapoints();

        if (data.size() > 0) {
            instance.setCpuUtilization(data.get(0).getAverage());
            //loadBalancer.cpuUtilization.put(instance.getInstance().getPublicIpAddress(), data.get(0).getAverage());
        }
    }

    @Override
    public void run() {
        for (Map.Entry<String, InstanceInfo> entry : loadBalancer.getInstanceSet()) {
            getCloudWatchMetrics(entry.getValue());

            System.out.println(entry.getValue().getCpuUtilization());
        }
    }
}
