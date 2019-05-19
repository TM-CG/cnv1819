package pt.ulisboa.tecnico.cnv.mss;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import pt.ulisboa.tecnico.cnv.common.Common;
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

    public double getMetrics(String query) {
        /**duvida -> devo retornar exatamente que cena zés? */
        HashMap<String, Condition> scanFilter = new HashMap<>();

        Map<String, String> args = Common.argumentsFromQuery(query);
        String id = "" + args.get("w") + args.get("h") + args.get("x0") + args.get("x1")
                + args.get("y1") + args.get("xS") + args.get("yS")
                + args.get("s") + args.get("i");

        System.out.println("ID " + id);

        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue(id));
        scanFilter.put("id", condition);

        ScanRequest scanRequest = new ScanRequest(TBL_NAME).withScanFilter(scanFilter);
        ScanResult scanResult = dynamoDB.scan(scanRequest);        

        if (scanResult.getItems().size() > 0) {
            Map<String, AttributeValue> metricLine = scanResult.getItems().get(0);
            return Double.parseDouble(metricLine.get("c").getS());
        } else {
            try {
                System.out.println("entrei");
                double searchArea = (Double.parseDouble(args.get("x1")) - Double.parseDouble(args.get("x0"))) * (Double.parseDouble(args.get("y1")) - Double.parseDouble(args.get("y0")));
                String searchMethod = new String(args.get("s"));
                int initX = Integer.parseInt(args.get("xS"));
                int initY = Integer.parseInt(args.get("yS"));
                System.out.println("SearchArea: " + searchArea + " SearchMethod: " + searchMethod + " initX: " + initX + " initY: " + initY);

                Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
                expressionAttributeValues.put(":maxH",  new AttributeValue(String.valueOf(Integer.parseInt(args.get("h"))+20)));
                expressionAttributeValues.put(":minH",  new AttributeValue(String.valueOf(Integer.parseInt(args.get("h"))-20)));

                ScanRequest sRequest = new ScanRequest()
                    .withTableName("metrics")
                    .withFilterExpression("h < :maxH and h > :minH")
                    .withProjectionExpression("area")
                    .withExpressionAttributeValues(expressionAttributeValues);
                
                ScanResult result = dynamoDB.scan(sRequest);
                System.out.println("elements matched in dynamodb: " + result.getCount());
                for (Map<String, AttributeValue> item : result.getItems()) {
                    System.out.println(item.get("area").getS());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }


            
        }
        return 800000;
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

    public PutItemResult addMetricObject(String tableName, String id, Metrics metric) {
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

    private Map<String, AttributeValue> newMetric(String id, Metrics metric) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("id",  new AttributeValue(id));
        item.put("bb",  new AttributeValue(String.valueOf(metric.basicBlocks())));
        item.put("bnt", new AttributeValue(String.valueOf(metric.getBranches())));
        item.put("w",   new AttributeValue(String.valueOf(metric.getWidth())));
        item.put("h",   new AttributeValue(String.valueOf(metric.getHeight())));
        item.put("x0",  new AttributeValue(String.valueOf(metric.getX0())));
        item.put("y0",  new AttributeValue(String.valueOf(metric.getY0())));
        item.put("x1",  new AttributeValue(String.valueOf(metric.getX1())));
        item.put("y1",  new AttributeValue(String.valueOf(metric.getY1())));
        item.put("xS",  new AttributeValue(String.valueOf(metric.getXS())));
        item.put("yS",  new AttributeValue(String.valueOf(metric.getYS())));
        item.put("a",   new AttributeValue(metric.getAlgorithm()));
        item.put("i",   new AttributeValue(metric.getMap()));
        item.put("c",   new AttributeValue(String.valueOf(cost(metric.basicBlocks(), metric.getBranches()))));
        item.put("area", new AttributeValue(String.valueOf((metric.getX1() - metric.getX0()) * (metric.getY1() - metric.getY0()))));

        return item;
    }

    public double cost(long bb,long bnt) {
        return ((bb*bnt)/(bb+bnt))/1000;
    }

}
