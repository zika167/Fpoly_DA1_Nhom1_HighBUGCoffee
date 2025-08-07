/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drink {
    private String id;
    private String name;
    private double unitPrice;
    private double discount;
    @Builder.Default
    private String image = "hinh2.jpg";
    private boolean available;
    private String categoryId;

    public double getDiscountedPrice() {
        return unitPrice * (1 - discount / 100.0);
    }

    @Override
    public String toString() {
        return "Drink{id=" + id + ", name=" + name + ", unitPrice=" + unitPrice +
               ", discount=" + discount + ", image=" + image + ", available=" + available +
               ", categoryId=" + categoryId + "}";
    }
}


