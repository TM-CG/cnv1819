package pt.ulisboa.tecnico.cnv.common;

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
}
