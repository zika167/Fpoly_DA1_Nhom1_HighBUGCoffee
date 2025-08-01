/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.Date;
import java.util.List;
import poly.cafe.dao.BillDAO;
import poly.cafe.entity.Bill;
import poly.cafe.util.XAuth;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class BillDAOImpl implements BillDAO {

    String createSql = "INSERT INTO Bills (Username, CardId, Checkin, Checkout, Status, PaymentId, ShopId) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Bills SET Username=?, CardId=?, Checkin=?, Checkout=?, Status=?, PaymentId=?, ShopId=? WHERE Id=?";
    String deleteSql = "DELETE FROM Bills WHERE Id=?";
    String findAllSql = "SELECT * FROM Bills";
    String findByIdSql = "SELECT * FROM Bills WHERE Id=?";
    String findByUsernameSql = "SELECT * FROM Bills WHERE Username=?";
    String findByCardIdSql = "SELECT * FROM Bills WHERE CardId=?";
    String findByTimeRangeSql = """
                                SELECT *
                                FROM Bills
                                WHERE checkin BETWEEN ? AND ?
                                ORDER BY Checkin DESC
                                """;

    @Override
    public List<Bill> findByTimeRange(Date begin, Date end) {
        return XQuery.getBeanList(Bill.class, findByTimeRangeSql, begin, end);
    }

    @Override
    public Bill create(Bill entity) {
      Object[] values = {
        entity.getUsername(),
        entity.getCardId(),
        entity.getCheckin(),
        entity.getCheckout(),
        entity.getStatus(),
        entity.getPaymentId(),
        entity.getShopId()
    };  
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }
    
    @Override
 public void update(Bill entity) {
     Object[] values = {
         entity.getUsername(),
         entity.getCardId(),
         entity.getCheckin(),
         entity.getCheckout(),
         entity.getStatus(),
         entity.getPaymentId(),
         entity.getShopId(),
         entity.getId() // Id bây giờ nằm ở cuối cho điều kiện WHERE
     };
     XJdbc.executeUpdate(updateSql, values);
 }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Bill> findAll() {
        return XQuery.getBeanList(Bill.class, findAllSql);
    }

    @Override
    public Bill findById(Long id) {
        return XQuery.getSingleBean(Bill.class, findByIdSql, id);
    }

    @Override
    public List<Bill> findByUsername(String username) {
        return XQuery.getBeanList(Bill.class, findByUsernameSql, username);
    }

    @Override
    public List<Bill> findByCardId(Integer cardId) {
        return XQuery.getBeanList(Bill.class, findByCardIdSql, cardId);
    }

    @Override
    public Bill findServicingByCardId(Integer cardId) {
        String sql = "SELECT * FROM Bills WHERE CardId=? AND Status=0";
        Bill bill = XQuery.getSingleBean(Bill.class, sql, cardId);
        if (bill == null) { // không tìm thấy -> tạo mới
            Bill newBill = new Bill();
            newBill.setCardId(cardId);
            newBill.setCheckin(new Date());
            newBill.setStatus(0); // đang phục vụ
            newBill.setUsername(XAuth.user.getUsername());
            newBill.setShopId(XAuth.user.getShopId()); // Set giá trị mặc định cho shopId (1L = Long value)
            newBill.setPaymentId(null); // Chưa thanh toán
            bill = this.create(newBill); // insert
        }
        return bill;
    }
    
    /*@Override
public Bill findServicingByCardId(Integer cardId) {
    String sql = "SELECT * FROM Bills WHERE CardId=? AND Status=0";
    Bill bill = XQuery.getSingleBean(Bill.class, sql, cardId);
    
    if (bill == null) { // không tìm thấy -> tạo mới
        Bill newBill = new Bill();
        
        // ----- PHẦN SỬA LỖI BẮT ĐẦU TỪ ĐÂY -----

        // BƯỚC 1: Kiểm tra xem người dùng đã đăng nhập chưa
        if (XAuth.user == null) {
            // Có thể thay bằngJOptionPane.showMessageDialog để thân thiện hơn
            throw new RuntimeException("Vui lòng đăng nhập để thực hiện chức năng này.");
        }

        // BƯỚC 2: Lấy ShopId và kiểm tra xem người dùng có thuộc chi nhánh không
        String shopId = XAuth.user.getShopId();
        if (shopId == null || shopId.trim().isEmpty()) {
            throw new RuntimeException("Tài khoản của bạn không có quyền tạo hóa đơn tại chi nhánh.");
        }
        
        // BƯỚC 3: Nếu mọi thứ hợp lệ, gán các giá trị cho hóa đơn mới
        newBill.setShopId(shopId); 
        newBill.setCardId(cardId);
        newBill.setCheckin(new Date());
        newBill.setStatus(0); // đang phục vụ
        newBill.setUsername(XAuth.user.getUsername());
        newBill.setPaymentId(null); // Chưa thanh toán

        // ----- KẾT THÚC PHẦN SỬA LỖI -----
        
        bill = this.create(newBill); // insert hóa đơn mới vào CSDL
    }
    return bill;
}*/

    @Override
    public List<Bill> findByUserAndTimeRange(String username, Date begin, Date end) {
        String sql = "SELECT * FROM Bills " + " WHERE Username=? AND Checkin BETWEEN ? AND ?";
        return XQuery.getBeanList(Bill.class, sql, username, begin, end);
    }

}
