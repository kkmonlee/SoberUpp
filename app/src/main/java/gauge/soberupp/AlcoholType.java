package gauge.soberupp;

/**
 * Created by hampe on 16 March 2017.
 */

public enum AlcoholType {
    BEER("Beer"), CIDER("Cider"), WINE("Wine"), SPIRITS("Spirits");
    private String name;
    private double abv;

    AlcoholType(String name) {
        this.name = name;
        switch (name) {
            case "Beer":
            case "Cider":
                this.abv = 4.5;
                break;
            case "Wine":
                this.abv = 12;
                break;
            case "Spirits":
                this.abv = 37.5;
                break;
            default:
                this.abv = 4;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAbv() {
        return abv;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }
}