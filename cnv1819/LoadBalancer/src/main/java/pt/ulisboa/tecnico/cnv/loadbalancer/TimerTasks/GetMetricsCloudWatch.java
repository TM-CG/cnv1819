package pt.ulisboa.tecnico.cnv.loadbalancer.TimerTasks;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import pt.ulisboa.tecnico.cnv.loadbalancer.LoadBalancer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GetMetricsCloudWatch extends TimerTask {

    private LoadBalancer loadBalancer;
    private AmazonCloudWatch cloudWatch;
    private Timer timer;

    public GetMetricsCloudWatch(LoadBalancer loadBalancer, AmazonCloudWatch cloudWatch) {
        this.loadBalancer = loadBalancer;
        this.cloudWatch = cloudWatch;
        timer = new Timer();
        timer.schedule(this, 1000 );
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
