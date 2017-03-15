package gauge.soberupp;

/**
 * Created by aa on 07 March 2017.
 * This class is the object to hold one single data entry of alcohol
 */

public class Alcohol {
    private String[] split;
    private double units;       // 10ml
    private String date;        // in the format dd-mm-yyyy
    private int id;             // starts at 1
    /*
      Name and type of alcohol will be added later
     */

    Alcohol() { }

    public Alcohol(int id, double units, String date) {
        this.id = id;
        this.units = units;
        this.date = date;
        split = this.date.split("-");
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDD() {
        return split[0];
    }

    public String getMM() {
        return split[1];
    }

    public String getYYYY() {
        return split[2];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
