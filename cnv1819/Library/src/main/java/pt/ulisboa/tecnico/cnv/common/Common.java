package pt.ulisboa.tecnico.cnv.common;

import pt.ulisboa.tecnico.cnv.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class Common {

    public static Map<String, String> argumentsFromQuery(String query) {
        HashMap<String, String> arguments = new HashMap<>();

        String[] args = query.split("&");
        for(String s : args){
            String[] paremeter = s.split("=");
            arguments.put(paremeter[0], paremeter[1]);
        }
        return arguments;
    }

    public static Metrics metricFromArguments(Map<String, String> arguments) {
        Metrics metrics = new Metrics();
        metrics.insertArgs(Integer.parseInt(arguments.get("w")), Integer.parseInt(arguments.get("h")),
                Integer.parseInt(arguments.get("x0")), Integer.parseInt(arguments.get("x1")),
                Integer.parseInt(arguments.get("y0")), Integer.parseInt(arguments.get("y1")),
                Integer.parseInt(arguments.get("xS")), Integer.parseInt(arguments.get("yS")),
                arguments.get("s"), arguments.get("i"));

        return metrics;
    }
}
