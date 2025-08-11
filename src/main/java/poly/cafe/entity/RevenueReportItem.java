package poly.cafe.entity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
/*
=====================================================================
Package: poly.cafe.entity
File:    RevenueReportItem.java
Mô tả:   Lớp đối tượng DTO (Data Transfer Object) để chứa dữ liệu
         thống kê doanh thu từ các câu lệnh SQL phức tạp.
=====================================================================
*/
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportItem {
    private String categoryName;
    private String drinkName;
    private int quantitySold;
    private double totalRevenue;
}
