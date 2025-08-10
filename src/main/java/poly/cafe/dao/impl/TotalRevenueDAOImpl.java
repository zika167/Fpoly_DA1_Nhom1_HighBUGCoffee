/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

/**
 *
 * @author User
 */



/*
=====================================================================
Package: poly.cafe.dao.impl
File:    RevenueDAOImpl.java
Mô tả:   Lớp triển khai các truy vấn SQL phức tạp để lấy dữ liệu
         thống kê doanh thu, sử dụng lớp tiện ích XJdbc.
=====================================================================
*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import poly.cafe.dao.TotalRevenueDAO;
import poly.cafe.entity.RevenueReportItem;
import poly.cafe.util.XJdbc;

public class TotalRevenueDAOImpl implements TotalRevenueDAO{

    @Override
    public List<RevenueReportItem> getRevenueByShopAndDateRange(String shopId, Date begin, Date end) {
        // Câu lệnh SQL này JOIN nhiều bảng để lấy được thông tin cần thiết:
        // - Lấy tổng số lượng và tổng doanh thu từ BillDetails.
        // - Lấy tên đồ uống và loại từ Drinks và Categories.
        // - Lọc theo ShopId thông qua bảng Users và Bills.
        // - Chỉ tính những hóa đơn đã hoàn thành (Status = 1).
        // - Lọc theo khoảng thời gian thanh toán (Checkout).
        String sql = """
            SELECT
                c.Name AS categoryName,
                d.Name AS drinkName,
                SUM(bd.Quantity) AS quantitySold,
                SUM(bd.Quantity * bd.UnitPrice * (1 - bd.Discount / 100.0)) AS totalRevenue
            FROM BillDetails bd
            JOIN Drinks d ON bd.DrinkId = d.Id
            JOIN Categories c ON d.CategoryId = c.Id
            JOIN Bills b ON bd.BillId = b.Id
            JOIN Users u ON b.Username = u.Username
            WHERE u.ShopId = ?
              AND b.Status = 1 
              AND b.Checkout BETWEEN ? AND ?
            GROUP BY c.Name, d.Name
            ORDER BY totalRevenue DESC;
        """;
        
        List<RevenueReportItem> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(sql, shopId, begin, end)) {
            while (rs.next()) {
                RevenueReportItem item = new RevenueReportItem();
                item.setCategoryName(rs.getString("categoryName"));
                item.setDrinkName(rs.getString("drinkName"));
                item.setQuantitySold(rs.getInt("quantitySold"));
                item.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(item);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu doanh thu", ex);
        }
        return list;
    }
}

