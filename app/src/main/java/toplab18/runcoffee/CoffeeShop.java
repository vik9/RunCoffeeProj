package toplab18.runcoffee;

import io.realm.RealmObject;

/**
 * Created by Vik-Note on 03.01.2018.
 */

public class CoffeeShop extends RealmObject {

    private String name;
    private String id;
    private String company;
    private String company_id;
    private String type;
    private String category;
    private String address;
    private Double lat;
    private Double lon;
    private String phone;
    private int rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {

        return name;

    }

    public void setTitle(final String name) {

        this.name = name;

    }

    @Override
    public String toString() {
        return "CoffeeShop{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", company='" + company + '\'' +
                ", company_id='" + company_id + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                '}';
    }
}

