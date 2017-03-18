package gauge.soberupp;

/**
 * Created by hampe on 16 March 2017.
 */

public enum AlcoholType {
    BEER("Beer"), CIDER("Cider"), WINE("Wine"), SPIRITS("Spirits"), OTHER("Other");
    private String name;

    AlcoholType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}