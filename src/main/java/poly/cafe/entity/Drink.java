/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

public class Drink {
    private String id;
    private String name;
    private double unitPrice;
    private double discount;
    private String image = "hinh2.jpg";
    private boolean available;
    private String categoryId;

    public Drink(String id, String name, String image, double unitPrice, double discount, boolean available, String categoryId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.available = available;
        this.categoryId = categoryId;
    }

    public Drink() {
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public static class Builder {
        private final Drink drink;

        public Builder() {
            drink = new Drink();
        }

        public Builder id(String id) {
            drink.setId(id);
            return this;
        }

        public Builder name(String name) {
            drink.setName(name);
            return this;
        }

        public Builder unitPrice(double unitPrice) {
            drink.setUnitPrice(unitPrice);
            return this;
        }

        public Builder discount(double discount) {
            drink.setDiscount(discount);
            return this;
        }

        public Builder image(String image) {
            drink.setImage(image);
            return this;
        }

        public Builder available(boolean available) {
            drink.setAvailable(available);
            return this;
        }

        public Drink build() {
            return drink;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return "Drink{id=" + id + ", name=" + name + ", unitPrice=" + unitPrice +
               ", discount=" + discount + ", image=" + image + ", available=" + available +
               ", categoryId=" + categoryId + "}";
    }
    
    public double getDiscountedPrice() {
        return unitPrice * (1 - discount / 100); // Tính giá sau giảm
    }
}

