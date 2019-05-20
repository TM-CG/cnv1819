package pt.ulisboa.tecnico.cnv.mss;

public class PairContainer {
    private String id;
    private Double cost;

    public PairContainer(String id, Double cost) {
        this.setId(id);
        this.setCost(cost);
    }

    public Double getCost() {
        return cost;
    }

    private void setCost(Double cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        PairContainer o = (PairContainer) obj;
        return o.getId().equals(this.id);
    }

}