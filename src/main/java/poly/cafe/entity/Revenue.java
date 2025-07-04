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
        private String category;  // Tên loại
        private double revenue;   // Doanh thu
        private int quantity;     // Số lượng đồ uống đã bán
        private double minPrice;  // Giá bán thấp nhất
        private double maxPrice;  // Giá bán cao nhất
        private double avgPrice;  // Giá bán trung bình
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ByUser {
        private String user;      // Tên đăng nhập của nhân viên
        private double revenue;   // Doanh thu
        private int quantity;     // Số lượng đơn hàng đã bán
        private Date firstTime;   // Đơn hàng đầu tiên
        private Date lastTime;    // Đơn hàng sau cùng
    }
}
//public class Revenue {
//    
////    @Builder
//    public static class ByCategory {
//
//        private String category; // Tên loại
//        private double revenue; // Doanh thu
//        private int quantity; // Số lượng đồ uống đã bán
//        private double minPrice; // Giá bán cao nhất
//        private double maxPrice; // Giá bán thấp nhất
//        private double avgPrice; // Giá bán trung bình
//
//        public ByCategory() {
//        }
//
//        public ByCategory(String category, double revenue, int quantity, double minPrice, double maxPrice, double avgPrice) {
//            this.category = category;
//            this.revenue = revenue;
//            this.quantity = quantity;
//            this.minPrice = minPrice;
//            this.maxPrice = maxPrice;
//            this.avgPrice = avgPrice;
//        }
//
//        public String getCategory() {
//            return category;
//        }
//
//        public void setCategory(String category) {
//            this.category = category;
//        }
//
//        public double getRevenue() {
//            return revenue;
//        }
//
//        public void setRevenue(double revenue) {
//            this.revenue = revenue;
//        }
//
//        public int getQuantity() {
//            return quantity;
//        }
//
//        public void setQuantity(int quantity) {
//            this.quantity = quantity;
//        }
//
//        public double getMinPrice() {
//            return minPrice;
//        }
//
//        public void setMinPrice(double minPrice) {
//            this.minPrice = minPrice;
//        }
//
//        public double getMaxPrice() {
//            return maxPrice;
//        }
//
//        public void setMaxPrice(double maxPrice) {
//            this.maxPrice = maxPrice;
//        }
//
//        public double getAvgPrice() {
//            return avgPrice;
//        }
//
//        public void setAvgPrice(double avgPrice) {
//            this.avgPrice = avgPrice;
//        }
//        
//        // Builder class
//        public static class Builder {
//            private String category;
//            private double revenue;
//            private int quantity;
//            private double minPrice;
//            private double maxPrice;
//            private double avgPrice;
//
//            public Builder() {
//            }
//
//            public Builder category(String category) {
//                this.category = category;
//                return this;
//            }
//
//            public Builder revenue(double revenue) {
//                this.revenue = revenue;
//                return this;
//            }
//
//            public Builder quantity(int quantity) {
//                this.quantity = quantity;
//                return this;
//            }
//
//            public Builder minPrice(double minPrice) {
//                this.minPrice = minPrice;
//                return this;
//            }
//
//            public Builder maxPrice(double maxPrice) {
//                this.maxPrice = maxPrice;
//                return this;
//            }
//
//            public Builder avgPrice(double avgPrice) {
//                this.avgPrice = avgPrice;
//                return this;
//            }
//
//            public ByCategory build() {
//                return new ByCategory(category, revenue, quantity, minPrice, maxPrice, avgPrice);
//            }
//        }
//        
//    }
//    
////    @Builder
//    public static class ByUser {
//        private String user; // Tên đăng nhập của nhân viên bán hàng
//        private double revenue; // Doanh thu
//        private int quantity; // Số lượng đơn hàng đã bán
//        private Date firstTime; // Thời điểm bán đơn hàng đầu tiên
//        private Date lastTime; // Thời điểm bán đơn hàng sau cùng
//
//        public ByUser() {
//        }
//
//        public ByUser(String user, double revenue, int quantity, Date firstTime, Date lastTime) {
//            this.user = user;
//            this.revenue = revenue;
//            this.quantity = quantity;
//            this.firstTime = firstTime;
//            this.lastTime = lastTime;
//        }
//
//        public String getUser() {
//            return user;
//        }
//
//        public void setUser(String user) {
//            this.user = user;
//        }
//
//        public double getRevenue() {
//            return revenue;
//        }
//
//        public void setRevenue(double revenue) {
//            this.revenue = revenue;
//        }
//
//        public int getQuantity() {
//            return quantity;
//        }
//
//        public void setQuantity(int quantity) {
//            this.quantity = quantity;
//        }
//
//        public Date getFirstTime() {
//            return firstTime;
//        }
//
//        public void setFirstTime(Date firstTime) {
//            this.firstTime = firstTime;
//        }
//
//        public Date getLastTime() {
//            return lastTime;
//        }
//
//        public void setLastTime(Date lastTime) {
//            this.lastTime = lastTime;
//        }
//        
//        // Builder class
//        public static class Builder {
//            private String user;
//            private double revenue;
//            private int quantity;
//            private java.util.Date firstTime;
//            private java.util.Date lastTime;
//
//            public Builder() {
//            }
//
//            public Builder user(String user) {
//                this.user = user;
//                return this;
//            }
//
//            public Builder revenue(double revenue) {
//                this.revenue = revenue;
//                return this;
//            }
//
//            public Builder quantity(int quantity) {
//                this.quantity = quantity;
//                return this;
//            }
//
//            public Builder firstTime(java.util.Date firstTime) {
//                this.firstTime = firstTime;
//                return this;
//            }
//
//            public Builder lastTime(java.util.Date lastTime) {
//                this.lastTime = lastTime;
//                return this;
//            }
//
//            public ByUser build() {
//                return new ByUser(user, revenue, quantity, firstTime, lastTime);
//            }
//        }
//        
//        // Phương thức tĩnh để tạo Builder
//        public static Builder builder() {
//            return new Builder();
//        }
//        
//    }
//
//}
