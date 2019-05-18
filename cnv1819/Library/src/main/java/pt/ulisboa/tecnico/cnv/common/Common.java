package pt.ulisboa.tecnico.cnv.common;

import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class Common {

    public static Map<String, String> argumentsFromQuery(String query) {
        HashMap<String, String> arguments = new HashMap<>();
        if (query != null) {
        String[] args = query.split("&");
            for(String s : args){
                String[] parameter = s.split("=");
                arguments.put(parameter[0], parameter[1]);
            }
        }

        return arguments;
    }

    public static Map<String, String> argumentsFromQuery(String query, Metrics metrics) {
        Map<String, String> arguments = argumentsFromQuery(query);
        arguments.put("bb", String.valueOf(metrics.getBasicBlocks()));
        arguments.put("br", String.valueOf(metrics.getBranches_notTaken()));

        return arguments;
    }

    public static Metrics metricFromArguments(Map<String, String> arguments) {
        Metrics metrics = new Metrics();
        metrics.insertArgs(Integer.parseInt(arguments.get("w")), Integer.parseInt(arguments.get("h")),
                Integer.parseInt(arguments.get("x0")), Integer.parseInt(arguments.get("x1")),
                Integer.parseInt(arguments.get("y0")), Integer.parseInt(arguments.get("y1")),
                Integer.parseInt(arguments.get("xS")), Integer.parseInt(arguments.get("yS")),
                arguments.get("s"), arguments.get("i"));

        if(arguments.containsKey("br") && arguments.containsKey("bb")){
            metrics.setBranches(Long.parseLong(arguments.get("br")));
            metrics.setBasicBlocks(Long.parseLong(arguments.get("bb")));
        }
        return metrics;
    }
}