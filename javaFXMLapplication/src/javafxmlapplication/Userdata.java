package javafxmlapplication;
public class Userdata {
    private String tinNumber;
    private String firstName;
    private String lastName;
    private String level;
    private double taxAmount;
    private String status;

    public Userdata(String tinNumber, String firstName, String lastName, String level, double taxAmount, String status) {
        this.tinNumber = tinNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.level = level;
        this.taxAmount = taxAmount;
        this.status = status;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
