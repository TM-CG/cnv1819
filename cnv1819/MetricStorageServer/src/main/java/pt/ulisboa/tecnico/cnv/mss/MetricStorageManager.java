package pt.ulisboa.tecnico.cnv.mss;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import pt.ulisboa.tecnico.cnv.common.Common;
import pt.ulisboa.tecnico.cnv.common.StaticConsts;
import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class MetricStorageManager {
    private AmazonDynamoDB dynamoDB;
    DynamodbCache cacheInstance;


    public MetricStorageManager(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.cacheInstance = DynamodbCache.getInstance();
        boolean f = true;
        while (f) {
            try {
                createTable(StaticConsts.TBL_NAME);
                System.out.println("Table " + StaticConsts.TBL_NAME + " created.");
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
        DynamodbCache cacheInstance = DynamodbCache.getInstance();
        cacheInstance.doPrint();
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

        ScanRequest scanRequest = new ScanRequest(StaticConsts.TBL_NAME).withScanFilter(scanFilter);
        ScanResult scanResult = dynamoDB.scan(scanRequest);        

        if(cacheInstance.containsElement(id)) {
            System.out.println("Cache hit!");
            return cacheInstance.getPair(id).getCost();
        }
        else if (scanResult.getItems().size() > 0) {
            Map<String, AttributeValue> metricLine = scanResult.getItems().get(0);
            return Double.parseDouble(metricLine.get("c").getN());
        } else {
            try {
                double searchArea = (Double.parseDouble(args.get("x1")) - Double.parseDouble(args.get("x0"))) * (Double.parseDouble(args.get("y1")) - Double.parseDouble(args.get("y0")));
                String searchMethod = args.get("s");
                String image = args.get("i");
                double initX = Double.parseDouble(args.get("xS"));
                double initY = Double.parseDouble(args.get("yS"));
                System.out.println("SearchArea: " + searchArea + " SearchMethod: " + searchMethod + " initX: " + initX + " initY: " + initY + " image: " + image);
                System.out.println("DEBUG maxInitialPointX " + (initX + 10) + "maxSearchArea " +(searchArea+2000) );

                Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
                expressionAttributeValues.put(":maxSearchArea",  new AttributeValue().withN(String.valueOf(searchArea+2000)));
                expressionAttributeValues.put(":minSearchArea",  new AttributeValue().withN(String.valueOf(searchArea-2000)));
                expressionAttributeValues.put(":maxInitialPointX", new AttributeValue().withN(String.valueOf(initX+10)));
                expressionAttributeValues.put(":minInitialPointX", new AttributeValue().withN(String.valueOf(initX-10)));
                expressionAttributeValues.put(":maxInitialPointY", new AttributeValue().withN(String.valueOf(initY+10)));
                expressionAttributeValues.put(":minInitialPointY", new AttributeValue().withN(String.valueOf(initY-10)));
                expressionAttributeValues.put(":searchA", new AttributeValue(searchMethod));
                expressionAttributeValues.put(":img", new AttributeValue(image));

                //.withFilterExpression("area < :maxSearchArea and area > :minSearchArea and a = :searchA and initX < :maxInitialPointX and initX > :minInitialPointX and initY < :maxInitialPointY and initY > :minInitialPointY")


                ScanRequest sRequest = new ScanRequest()
                    .withTableName("metrics")
                    .withFilterExpression("area < :maxSearchArea and area > :minSearchArea and a = :searchA and xS < :maxInitialPointX and xS > :minInitialPointX and yS < :maxInitialPointY and yS > :minInitialPointY and i = :img")
                    .withProjectionExpression("c")
                    .withExpressionAttributeValues(expressionAttributeValues);
                
                ScanResult result = dynamoDB.scan(sRequest);
                System.out.println("elements matched in dynamodb: " + result.getCount());
                double estimatedCost = 0;
                for (Map<String, AttributeValue> item : result.getItems()) {
                    estimatedCost+=Double.parseDouble(item.get("c").getN());
                    System.out.println(item.get("c").getN());
                }
                if(estimatedCost==0) {
                    return StaticConsts.DEFAULT_COST;
                } else {
                    estimatedCost /= result.getCount();
                    System.out.println("Final cost: "+estimatedCost);
                    return estimatedCost;
                }
            } catch(Exception e) {
                e.printStackTrace();
                return StaticConsts.DEFAULT_COST;
            }
        }
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
        System.out.println("entrei");
        Map<String, AttributeValue> item = new HashMap<>();
        double cost = cost(metric.basicBlocks(), metric.getBranches());
        System.out.println("DEBUG: "+cost);
        item.put("id",  new AttributeValue(id));
        item.put("bb",  new AttributeValue().withN(String.valueOf(metric.basicBlocks())));
        item.put("bnt", new AttributeValue().withN(String.valueOf(metric.getBranches())));
        item.put("w",   new AttributeValue().withN(String.valueOf(metric.getWidth())));
        item.put("h",   new AttributeValue().withN(String.valueOf(metric.getHeight())));
        item.put("x0",  new AttributeValue().withN(String.valueOf(metric.getX0())));
        item.put("y0",  new AttributeValue().withN(String.valueOf(metric.getY0())));
        item.put("x1",  new AttributeValue().withN(String.valueOf(metric.getX1())));
        item.put("y1",  new AttributeValue().withN(String.valueOf(metric.getY1())));
        item.put("xS",  new AttributeValue().withN(String.valueOf(metric.getXS())));
        item.put("yS",  new AttributeValue().withN(String.valueOf(metric.getYS())));
        item.put("a",   new AttributeValue(metric.getAlgorithm()));
        item.put("i",   new AttributeValue(metric.getMap()));
        item.put("c",   new AttributeValue().withN(String.valueOf(cost)));
        item.put("area", new AttributeValue().withN(String.valueOf((metric.getX1() - metric.getX0()) * (metric.getY1() - metric.getY0()))));

        cacheInstance.addElement(new PairContainer(id, cost));

        return item;
    }

    public double cost(long bb,long bnt) {
        return ((bb*bnt)/(bb+bnt))/1000;
    }

}
