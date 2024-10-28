

package javafxmlapplication;

public class user {
    private String tinNumber;
    private String firstName;
    private String lastName;
    private String level;
    private Double taxAmount;
    private String phone;
    private String status;
    private String imageURL; 

    public user(String tinNumber, String firstName, String lastName, String level, Double taxAmount, String phone, String status, String imageURL) {
        this.tinNumber = tinNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.level = level;
        this.taxAmount = taxAmount;
        this.phone = phone;
        this.status = status;
        this.imageURL = imageURL;
    }

  
    public String getTin_number() {
        return tinNumber;
    }

    public void setTin_number(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getFirst_name() {
        return firstName;
    }

    public void setFirst_name(String firstName) {
        this.firstName = firstName;
    }

    public String getLast_name() {
        return lastName;
    }

    public void setLast_name(String lastName) {
        this.lastName = lastName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getTax_Amount() {
        return taxAmount;
    }

    public void setTax_Amount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIMG_url() {
        return imageURL;
    }

    public void setIMG_url(String imageURL) {
        this.imageURL = imageURL;
    }
}

