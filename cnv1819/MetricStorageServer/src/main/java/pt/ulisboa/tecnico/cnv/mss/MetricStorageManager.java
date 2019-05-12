package pt.ulisboa.tecnico.cnv.mss;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.glue.model.Table;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class MetricStorageManager {
    public static final String TBL_NAME = "metrics";

    private AmazonDynamoDB dynamoDB;

    public MetricStorageManager(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        boolean f = true;
        while (f) {
            try {
                createTable(TBL_NAME);
                System.out.println("Table " + TBL_NAME + " created.");
                f = false;
            } catch (InterruptedException e) {
                System.out.println("Could not create the table. Will try again.");
            }
        }

    }

    /**
     * Creates a table on DynamoDB
     *
     * @param tableName the table name
     */
    public void createTable(String tableName) throws InterruptedException {
        // Create a table with a primary hash key named 'name', which holds a string
        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withKeySchema(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("id").withAttributeType(ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

        // Create table if it does not exist yet
        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
        // wait for the table to move into ACTIVE state
        TableUtils.waitUntilActive(dynamoDB, tableName);

    }

    /**
     * Returns the table description
     *
     * @param tableName the name of the table to get the description
     * @return
     */
    public TableDescription tableDescription(String tableName) {
        // Describe our new table
        DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
        return dynamoDB.describeTable(describeTableRequest).getTable();
    }

    public PutItemResult addMetric(String tableName, String metricName, String value) {
        // Add an item
        Map<String, AttributeValue> item = newMetric(metricName, value);
        PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
        return dynamoDB.putItem(putItemRequest);
    }

    public PutItemResult addMetricObject(String tableName, long id, Metrics metric) {
        // Add an item
        Map<String, AttributeValue> item = newMetric(id, metric);
        PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
        return dynamoDB.putItem(putItemRequest);

    }

    private Map<String, AttributeValue> newMetric(String metricName, String value) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("metricName", new AttributeValue(metricName));
        item.put("value", new AttributeValue(value));

        return item;
    }

    private Map<String, AttributeValue> newMetric(long id, Metrics metric) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("id", new AttributeValue(String.valueOf(id)));
        item.put("bb", new AttributeValue(String.valueOf(metric.basicBlocks())));
        item.put("bnt", new AttributeValue(String.valueOf(metric.getBranches())));
        item.put("w", new AttributeValue(String.valueOf(metric.getWidth())));
        item.put("h", new AttributeValue(String.valueOf(metric.getHeight())));
        item.put("x0", new AttributeValue(String.valueOf(metric.getX0())));
        item.put("y0", new AttributeValue(String.valueOf(metric.getY0())));
        item.put("x1", new AttributeValue(String.valueOf(metric.getX1())));
        item.put("y1", new AttributeValue(String.valueOf(metric.getY1())));
        item.put("xS", new AttributeValue(String.valueOf(metric.getXS())));
        item.put("yS", new AttributeValue(String.valueOf(metric.getYS())));
        item.put("a", new AttributeValue(metric.getAlgorithm()));
        item.put("m", new AttributeValue(metric.getMap()));

        return item;
    }

}
