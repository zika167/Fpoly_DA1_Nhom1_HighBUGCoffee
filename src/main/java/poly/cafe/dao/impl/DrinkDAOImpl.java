/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.entity.Drink;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class DrinkDAOImpl implements DrinkDAO {

    String createSql = "INSERT INTO Drinks (Id, Name, Image, UnitPrice, Discount, Available, CategoryId) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Drinks SET Name=?, Image=?, UnitPrice=?, Discount=?, Available=?, CategoryId=? WHERE Id=?";
    String deleteSql = "DELETE FROM Drinks WHERE Id=?";
    String findAllSql = "SELECT * FROM Drinks";
    String findByIdSql = "SELECT * FROM Drinks WHERE Id=?";
    String findByCategoryIdSql = "SELECT * FROM Drinks WHERE CategoryId=?";

    @Override
    public Drink create(Drink entity) {
        Object[] values = {
            entity.getId(),
            entity.getName(),
            entity.getImage(),
            entity.getUnitPrice(),
            entity.getDiscount(),
            entity.isAvailable(),
            entity.getCategoryId()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Drink entity) {
        Object[] values = {
            entity.getName(),
            entity.getImage(),
            entity.getUnitPrice(),
            entity.getDiscount(),
            entity.isAvailable(),
            entity.getCategoryId(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Drink> findAll() {
        System.out.println("Executing query: " + findAllSql);
        List<Drink> list = XQuery.getBeanList(Drink.class, findAllSql);
        System.out.println("Number of drinks found: " + list.size());
        if (list.isEmpty()) {
            System.out.println("No drinks retrieved. Check database or SQL query.");
        } else {
            list.forEach(drink -> System.out.println("Drink: " + drink));
        }
        return list;
    }

    @Override
    public Drink findById(String id) {
        return XQuery.getSingleBean(Drink.class, findByIdSql, id);
    }

    @Override
    public List<Drink> findByCategoryId(String categoryId) {
        System.out.println("Executing query for CategoryId: " + categoryId + " with SQL: " + findByCategoryIdSql);
        List<Drink> drinks = XQuery.getBeanList(Drink.class, findByCategoryIdSql, categoryId);
        System.out.println("Number of drinks found for CategoryId " + categoryId + ": " + drinks.size());
        if (drinks.isEmpty()) {
            System.out.println("No drinks found for CategoryId " + categoryId + ". Check category or data.");
        } else {
            drinks.forEach(drink -> System.out.println("Drink: " + drink));
        }
        return drinks;
    }
}
