/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.entity.BillDetail;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class BillDetailDAOImpl implements BillDetailDAO {

    String createSql = "INSERT INTO BillDetails (Id, BillId, DrinkId, UnitPrice, Discount, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE BillDetails SET BillId=?, DrinkId=?, UnitPrice=?, Discount=?, Quantity=? WHERE Id=?";
    String deleteSql = "DELETE FROM BillDetails WHERE Id=?";
    
    String findAllSql = 
            """
                SELECT 
                    bd.*
                    ,d.name as drinkName
                FROM BillDetails bd
                JOIN Drinks d ON bd.DrinkId = d.Id
            """;
    String findByIdSql = 
            """
                SELECT 
                    bd.*
                    ,d.name AS drinkName 
                FROM BillDetails bd 
                JOIN Drinks d ON d.Id = bd.DrinkId
                WHERE Id=?
            """;
    String findByBillIdSql = 
            """
                SELECT 
                    bd.*
                    ,d.name AS drinkName
                FROM BillDetails bd
                JOIN Drinks d ON d.Id = bd.DrinkId
                WHERE BillId=?
            """;
    String findByDrinkIdSql = 
            """
                SELECT
                    bd.*
                    ,d.name AS drinkName
                FROM BillDetails bd
                JOIN Drinks d ON d.Id = bd.DrinkId
                WHERE DrinkId=?
            """;

    @Override
    public BillDetail create(BillDetail entity) {
        Object[] values = {
            entity.getId(),
            entity.getBillId(),
            entity.getDrinkId(),
            entity.getUnitPrice(),
            entity.getDiscount(),
            entity.getQuantity()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(BillDetail entity) {
        Object[] values = {
            entity.getBillId(),
            entity.getDrinkId(),
            entity.getUnitPrice(),
            entity.getDiscount(),
            entity.getQuantity(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<BillDetail> findAll() {
        return XQuery.getBeanList(BillDetail.class, findAllSql);
    }

    @Override
    public BillDetail findById(Long id) {
        return XQuery.getSingleBean(BillDetail.class, findByIdSql, id);
    }

    /*@Override
    public List<BillDetail> findByBillId(Long billId) {
        return XQuery.getBeanList(BillDetail.class, findByBillIdSql, billId);
    }*/

   @Override
public List<BillDetail> findByBillId(Long billId) {
    String sql = """
                 SELECT bd.*, d.Name AS DrinkName
                 FROM BillDetails bd
                 JOIN Drinks d ON bd.DrinkId = d.Id
                 WHERE bd.BillId = ?
                 """;
    return poly.cafe.util.XQuery.getBeanList(BillDetail.class, sql, billId);
}
    
    @Override
    public List<BillDetail> findByDrinkId(String drinkId) {
        return XQuery.getBeanList(BillDetail.class, findByDrinkIdSql, drinkId);
    }
}
