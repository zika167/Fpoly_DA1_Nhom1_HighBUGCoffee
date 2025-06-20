/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.util;

import java.sql.ResultSet;
import java.util.List;
import poly.cafe.entity.Category;

/**
 *
 * @author wangquockhanh
 */
public class Test {
    public static void main(String[] args) {
        // Thêm mới
        String sqlInsert = "INSERT INTO Categories (Id, Name) VALUES(?, ?)";
        XJdbc.executeUpdate(sqlInsert, "C01", "Loại 1");
        XJdbc.executeUpdate(sqlInsert, "C02", "Loại 2");

        // Truy vấn nhiều bản ghi
        String sqlQuery = "SELECT * FROM Categories WHERE Name LIKE ?";
        ResultSet rs = XJdbc.executeQuery(sqlQuery, "%Loại%");

        // Truy vấn nhiều bản ghi và chuyển sang List<Category>
        List<Category> list = XQuery.getBeanList(Category.class, sqlQuery, "%Loại%");
        for (Category c : list) {
            System.out.println(c.getId() + " - " + c.getName());
        }

        // Truy vấn 1 bản ghi
        String sqlSingle = "SELECT * FROM Categories WHERE Id=?";
        Category cat = XQuery.getSingleBean(Category.class, sqlSingle, "C02");
        System.out.println("Single: " + cat.getId() + " - " + cat.getName());

        // Truy vấn 1 giá trị
        String sqlValue = "SELECT max(Id) FROM Categories WHERE Name LIKE ?";
        String maxId = String.valueOf(XJdbc.getValue(sqlValue, "%Loại%"));
        System.out.println("Max ID: " + maxId);
    }
}
