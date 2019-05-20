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
        size = 20;
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
        index = (index+1)%(size);
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
            try {
                entry.set(this.index, p);
            }
            catch (Exception e) {
                entry.add(this.index,p);
            }
            System.out.println("Added element to cache on position " + this.index);
            incrementIndex();
        }
        else {
            System.out.println("Element already in cache!");
        }
    }

    public void doPrint() {
        System.out.println("==========");
        for(PairContainer p : entry){
            System.out.println("ID: " + p.getId() + " COST: " + p.getCost());
        }
        System.out.println("==========");

    }
}