package toplab18.runcoffee;

import io.realm.RealmObject;

public class MenuCard extends RealmObject{

private String id;
private String name;
private String measure;
private String size;
private String price;

private String currency;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getMeasure() {
return measure;
}

public void setMeasure(String measure) {
this.measure = measure;
}

public String getSize() {
return size;
}

public void setSize(String size) {
this.size = size;
}

public String getPrice() {
return price;
}

public void setPrice(String price) {
this.price = price;
}

public String getCurrency() {
return currency;
}

public void setCurrency(String currency) {
this.currency = currency;
}

    @Override
    public String toString() {
        return "MenuCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", measure='" + measure + '\'' +
                ", size='" + size + '\'' +
                ", price='" + price + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}