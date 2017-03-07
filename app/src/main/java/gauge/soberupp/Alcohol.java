package gauge.soberupp;

/**
 * Created by aa on 07 March 2017.
 * This class is the object to hold one single data entry of alcohol
 */

public class Alcohol {
    private String[] split;
    private double units;        // in pints
    private String date;        // in the format dd-mm-yyyy
    /*
      Name and type of alcohol will be added later
     */

    public Alcohol(double units, String date) {
        this.units = units;
        this.date = date;
        split = this.date.split("-");
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(float units) {
        this.units = units;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLitres() {
        return this.units * 0.568261;
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
}
