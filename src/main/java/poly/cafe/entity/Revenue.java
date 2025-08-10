/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author wangquockhanh
 */

public class Revenue {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ByCategory {
        private String category; // Tên loại
        private String drinkName; // Tên đồ uống
        private int quantity; // Số lượng đồ uống đã bán
        private double revenue; // Doanh thu
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ByUser {
        private String user; // Mã nhân viên (Username)
        private String employeeName; // Tên nhân viên
        private int quantity; // Số lượng bill đã bán
        private double revenue; // Doanh thu
    }
}
