package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;

import java.util.Date;

public class GetMetricsCloudWatch extends GenericTimeTask {

    private AmazonCloudWatch cloudWatch;

    public GetMetricsCloudWatch(LoadBalancer loadBalancer, AmazonCloudWatch cloudWatch, int seconds) {
        super(loadBalancer, seconds);
        this.cloudWatch = cloudWatch;
    }

    public void getCloudWatchMetrics() {

        long offsetInMilliseconds = 1000 * 60;
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
                .withPeriod(60)
                .withEndTime(new Date())
                .withNamespace("AWS/EC2")
                .withDimensions(new Dimension().withName("ImageId").withValue("ami-09def150731bdbcc2"))
                .withMetricName("CPUUtilization")
                .withStatistics("Average", "Maximum");
        GetMetricStatisticsResult result = cloudWatch.getMetricStatistics(request);

        System.out.println(result.getLabel() + ": " + result.getDatapoints() );
    }

    @Override
    public void run() {
        getCloudWatchMetrics();

    }
}
