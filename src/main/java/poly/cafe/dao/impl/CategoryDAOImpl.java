/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.entity.Category;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class CategoryDAOImpl implements CategoryDAO {

    String createSql = "INSERT INTO Categories(Id, Name) VALUES(?, ?)";
    String updateSql = "UPDATE Categories SET Name=? WHERE Id=?";
    String deleteSql = "DELETE FROM Categories WHERE Id=?";
    String findAllSql = "SELECT * FROM Categories";
    String findByIdSql = "SELECT * FROM Categories WHERE Id=?";

    @Override
    public Category create(Category entity) {
        Object[] values = {
            entity.getId(),
            entity.getName()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }
    @Override
    public void update(Category entity) {
        Object[] values = {
            entity.getName(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }
    
    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }
    
    @Override
    public List<Category> findAll() {
        System.out.println("Executing query: " + findAllSql);
        List<Category> list = new ArrayList<>();
        try {
            ResultSet resultSet = XJdbc.executeQuery(findAllSql);
            System.out.println("ResultSet opened, starting loop...");
            while (resultSet.next()) {
                System.out.println("Processing row...");
                Category category = new Category();
                category.setId(resultSet.getString("Id"));   // Tên cột trong DB
                category.setName(resultSet.getString("Name"));
                list.add(category);
            }
            System.out.println("Loop completed, number of categories: " + list.size());
        } catch (Exception e) {
            System.out.println("Error in findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public Category findById(String id) {
        return XQuery.getSingleBean(Category.class, findByIdSql, id);
    }
}

