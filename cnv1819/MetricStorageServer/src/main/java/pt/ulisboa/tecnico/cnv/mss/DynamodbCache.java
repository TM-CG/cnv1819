package pt.ulisboa.tecnico.cnv.mss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DynamodbCache {
    private int size;
    private int index;
    private List<PairContainer> entry;
    private static DynamodbCache single_instance = null;

    private DynamodbCache() {
        index = 0;
        size = 2;
        entry = Collections.synchronizedList(new ArrayList<PairContainer>(size)); 
    }

    public static DynamodbCache getInstance() {
        if(single_instance == null) {
            single_instance = new DynamodbCache();
        }
        return single_instance;
    }

    private int getIndex() {
        return index;
    }

    private void incrementIndex() {
        index = index%(size);
        index++; 
    }

    public boolean containsElement(String s) {
        PairContainer p = new PairContainer(s, 0.0);
        return entry.contains(p);
    }

    public PairContainer getPair(String s) {
        PairContainer p = new PairContainer(s, 0.0);
        int ind = entry.indexOf(p);
        if(ind!=-1) {
            return entry.get(ind);
        } else {
            return null;
        }
    }

    public void addElement(PairContainer p) {

        if(entry.indexOf(p)==-1) {
            entry.add(this.index,p);
            incrementIndex();
            System.out.println("Added element to cache!");
        }
        else {
            System.out.println("Element already in cache!");
        }
    }
}