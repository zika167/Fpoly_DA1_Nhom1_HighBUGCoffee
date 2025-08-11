/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.EmployeeDAO;
import poly.cafe.entity.Employee;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author LENOVO
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public Employee create(Employee employee) {
        String sql = "INSERT INTO Users (Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            XJdbc.executeUpdate(sql, 
                employee.getUsername(),
                employee.getPassword(),
                employee.isActive(),
                employee.getFullname(),
                "default_avatar.jpg", // Default photo
                "branch_manager", // Default role for branch managers
                null, // ShopId will be set later when assigned to a shop
                employee.getPhone()
            );
            return employee;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Employee employee) {
        String sql = "UPDATE Users SET Password = ?, Enabled = ?, Fullname = ?, Phone = ? WHERE Username = ?";
        try {
            return XJdbc.executeUpdate(sql, 
                employee.getPassword(),
                employee.isActive(),
                employee.getFullname(),
                employee.getPhone(),
                employee.getUsername()
            ) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String username) {
        String sql = "DELETE FROM Users WHERE Username = ?";
        try {
            return XJdbc.executeUpdate(sql, username) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone FROM Users WHERE Role = 'branch_manager'";
        List<Employee> employees = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql);
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setUsername(rs.getString("Username"));
                employee.setPassword(rs.getString("Password"));
                employee.setActive(rs.getBoolean("Enabled"));
                employee.setFullname(rs.getString("Fullname"));
                employee.setPhone(rs.getString("Phone"));
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public Employee findByFullname(String fullname) {
        String sql = "SELECT Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone FROM Users WHERE Fullname = ? AND Role = 'branch_manager'";
        try {
            ResultSet rs = XJdbc.executeQuery(sql, fullname);
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setUsername(rs.getString("Username"));
                employee.setPassword(rs.getString("Password"));
                employee.setActive(rs.getBoolean("Enabled"));
                employee.setFullname(rs.getString("Fullname"));
                employee.setPhone(rs.getString("Phone"));
                return employee;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Employee findByUsername(String username) {
        String sql = "SELECT Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone FROM Users WHERE Username = ? AND Role = 'branch_manager'";
        try {
            ResultSet rs = XJdbc.executeQuery(sql, username);
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setUsername(rs.getString("Username"));
                employee.setPassword(rs.getString("Password"));
                employee.setActive(rs.getBoolean("Enabled"));
                employee.setFullname(rs.getString("Fullname"));
                employee.setPhone(rs.getString("Phone"));
                return employee;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Employee> findByShopId(String shopId) {
        String sql = "SELECT Username, Password, Enabled, Fullname, Photo, Role, ShopId, Phone FROM Users WHERE ShopId = ? AND Role = 'branch_manager'";
        List<Employee> employees = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, shopId);
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setUsername(rs.getString("Username"));
                employee.setPassword(rs.getString("Password"));
                employee.setActive(rs.getBoolean("Enabled"));
                employee.setFullname(rs.getString("Fullname"));
                employee.setPhone(rs.getString("Phone"));
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }
    
    @Override
    public String getRoleByUsername(String username) {
        String sql = "SELECT Role FROM Users WHERE Username = ?";
        try {
            ResultSet rs = XJdbc.executeQuery(sql, username);
            if (rs.next()) {
                String role = rs.getString("Role");
                return role;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public String getShopIdByUsername(String username) {
        String sql = "SELECT ShopId FROM Users WHERE Username = ?";
        try {
            ResultSet rs = XJdbc.executeQuery(sql, username);
            if (rs.next()) {
                String shopId = rs.getString("ShopId");
                return shopId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
