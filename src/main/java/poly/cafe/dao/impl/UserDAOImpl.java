/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.UserDAO;
import poly.cafe.entity.User;
import poly.cafe.util.XJdbc;

/**
 *
 * @author wangquockhanh
 */
public class UserDAOImpl implements UserDAO {

    String createSql = "INSERT INTO Users (Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    String updateSql = "UPDATE Users SET Password = ?, Enabled = ?, Fullname = ?, Photo = ?, Role = ?, ShopId = ?, Phone = ? WHERE Username = ?";

    String deleteSql = "DELETE FROM Users WHERE Username = ?";
    String findAllSql = "SELECT * FROM Users";
    String findByIdSql = "SELECT * FROM Users WHERE Username = ?";
    String findByFullnameSql = "SELECT * FROM Users WHERE Fullname = ?";

    @Override
    public User create(User entity) {
        Object[] values = {
                entity.getUsername(),
                entity.getPassword(),
                entity.isEnabled(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.getRole() != null ? entity.getRole().name() : null, // Chuyển Enum thành String
                entity.getShopId(), // Thêm ShopId vào values
                entity.getPhone() // Thêm Phone vào values
        };
        System.out.println("Creating user in database with ShopId: " + entity.getShopId());
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
                entity.getRole() != null ? entity.getRole().name() : null, // Chuyển Enum thành String
                entity.getShopId(), // Thêm ShopId vào values
                entity.getPhone(), // Thêm Phone vào values
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
        List<User> users = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(findAllSql)) {
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEnabled(rs.getBoolean("Enabled"));
                user.setFullname(rs.getString("Fullname"));
                user.setPhoto(rs.getString("Photo"));
                user.setPhone(rs.getString("Phone"));
                
                String roleStr = rs.getString("Role");
                if (roleStr != null) {
                    user.setRole(User.Role.valueOf(roleStr));
                }
                
                user.setShopId(rs.getString("ShopId"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User findById(String username) {
        try (ResultSet rs = XJdbc.executeQuery(findByIdSql, username)) {
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEnabled(rs.getBoolean("Enabled"));
                user.setFullname(rs.getString("Fullname"));
                user.setPhoto(rs.getString("Photo"));
                user.setPhone(rs.getString("Phone"));
                
                String roleStr = rs.getString("Role");
                if (roleStr != null) {
                    user.setRole(User.Role.valueOf(roleStr));
                }
                
                user.setShopId(rs.getString("ShopId"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public User findByFullname(String fullname) {
        try (ResultSet rs = XJdbc.executeQuery(findByFullnameSql, fullname)) {
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEnabled(rs.getBoolean("Enabled"));
                user.setFullname(rs.getString("Fullname"));
                user.setPhoto(rs.getString("Photo"));
                user.setPhone(rs.getString("Phone"));
                
                String roleStr = rs.getString("Role");
                if (roleStr != null) {
                    user.setRole(User.Role.valueOf(roleStr));
                }
                
                user.setShopId(rs.getString("ShopId"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}