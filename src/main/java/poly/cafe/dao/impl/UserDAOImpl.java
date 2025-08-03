/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.UserDAO;
import poly.cafe.entity.User;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class UserDAOImpl implements UserDAO {

    String createSql = "INSERT INTO Users (Username, Password, Enabled, Fullname, Photo, Role) VALUES (?, ?, ?, ?, ?, ?)";

    String updateSql = "UPDATE Users SET Password = ?, Enabled = ?, Fullname = ?, Photo = ?, Role = ? WHERE Username = ?";

    String deleteSql = "DELETE FROM Users WHERE Username = ?";
    String findAllSql = "SELECT * FROM Users";
    String findByIdSql = "SELECT * FROM Users WHERE Username = ?";
    
    @Override
    public User create(User entity) {
        Object[] values = {
            entity.getUsername(),
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.getRole() // <-- thêm role thay vì isManager()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(User entity) {
        Object[] values = {
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.getRole(), // <-- sửa lại ở đây
            entity.getUsername()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String username) {
        XJdbc.executeUpdate(deleteSql, username);
    }

    @Override
    public List<User> findAll() {
        return XQuery.getBeanList(User.class, findAllSql);
    }

    @Override
    public User findById(String username) {
        return XQuery.getSingleBean(User.class, findByIdSql, username);
    }
}


