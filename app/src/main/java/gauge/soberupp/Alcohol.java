package gauge.soberupp;

/**
 * Created by aa on 07 March 2017.
 * This class is the object to hold one single data entry of alcohol
 */

public class Alcohol {
    private String[] split;             // holds date
    private double units;               // 10ml
    private String date;                // in the format dd-mm-yyyy
    private int id;                     // starts at 1
    private double quantity;            // number of cans/shots/glasses
    private double volume;              // volume of alcohol
    private AlcoholType alcoholType;    // type of alcohol

    Alcohol() { }

    public Alcohol(int id, String date, AlcoholType alcoholType, double volume, double quantity) {
        this.id = id;
        this.date = date;
        this.alcoholType = alcoholType;
        this.volume = volume;
        this.quantity = quantity;
        this.units = (this.volume * this.alcoholType.getAbv() * this.quantity) / 1000;
        split = this.date.split("-");
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public void calculateUnits(){this.units = (this.volume * this.alcoholType.getAbv() * this.quantity) / 1000;}

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


    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setAlcoholType(AlcoholType alcoholType) {
        this.alcoholType = alcoholType;
    }

    public AlcoholType getAlcoholType() {
        return alcoholType;
    }
}
