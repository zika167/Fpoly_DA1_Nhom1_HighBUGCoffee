/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.PaymentDAO;
import poly.cafe.entity.Payment;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class PaymentDAOImpl implements PaymentDAO {

    String createSql = "INSERT INTO Payments (PaymentMethod, Amount, PaymentDate) VALUES (?, ?, ?)";
    String updateSql = "UPDATE Payments SET PaymentMethod = ?, Amount = ?, PaymentDate = ? WHERE Id = ?";
    String deleteSql = "DELETE FROM Payments WHERE Id = ?";
    String findAllSql = "SELECT * FROM Payments";
    String findByIdSql = "SELECT * FROM Payments WHERE Id = ?";

    @Override
    public Payment create(Payment entity) {
        Object[] values = {
            entity.getPaymentMethod(),
            entity.getAmount(),
            entity.getPaymentDate()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Payment entity) {
        Object[] values = {
            entity.getPaymentMethod(),
            entity.getAmount(),
            entity.getPaymentDate(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Payment> findAll() {
        return XQuery.getBeanList(Payment.class, findAllSql);
    }

    @Override
    public Payment findById(Long id) {
        return XQuery.getSingleBean(Payment.class, findByIdSql, id);
    }

} 