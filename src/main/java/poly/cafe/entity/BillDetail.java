/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BillDetail {
    private Long id;
    private Long billId;
    private String drinkId;
    private double unitPrice;
    private double discount;
    private int quantity;
    private String drinkName;
}

/*package poly.cafe.entity;

import java.math.BigDecimal; // Thêm import cho BigDecimal
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDetail {
    private Long id;
    private Long billId;
    private String drinkId;
    
    // Thay thế double bằng BigDecimal để đảm bảo tính toán chính xác
    private BigDecimal unitPrice;
    private BigDecimal discount;
    
    private int quantity;
    private String drinkName;
}*/
