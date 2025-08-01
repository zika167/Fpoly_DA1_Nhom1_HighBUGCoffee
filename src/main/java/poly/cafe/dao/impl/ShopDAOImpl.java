/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.ShopDAO;
import poly.cafe.entity.Shop;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class ShopDAOImpl implements ShopDAO {

    String createSql = "INSERT INTO Shops (Id, ShopName, Address, ManagerUsername) VALUES (?, ?, ?, ?)";
    String updateSql = "UPDATE Shops SET ShopName = ?, Address = ?, ManagerUsername = ? WHERE Id = ?";
    String deleteSql = "DELETE FROM Shops WHERE Id = ?";
    String findAllSql = "SELECT * FROM Shops";
    String findByIdSql = "SELECT * FROM Shops WHERE Id = ?";

    @Override
    public Shop create(Shop entity) {
        Object[] values = {
            entity.getId(),
            entity.getShopName(),
            entity.getAddress(),
            entity.getManagerUsername()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Shop entity) {
        Object[] values = {
            entity.getShopName(),
            entity.getAddress(),
            entity.getManagerUsername(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Shop> findAll() {
        return XQuery.getBeanList(Shop.class, findAllSql);
    }

    @Override
    public Shop findById(String id) {
        return XQuery.getSingleBean(Shop.class, findByIdSql, id);
    }

} 