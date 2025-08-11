/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.entity.Employee;
/**
 *
 * @author LENOVO
 */
public interface EmployeeDAO {
    Employee create(Employee employee);
    boolean update(Employee employee);
    boolean delete(String username);
    List<Employee> findAll();
    Employee findByFullname(String fullname);
    Employee findByUsername(String username);
    List<Employee> findByShopId(String shopId);
    String getRoleByUsername(String username);
    String getShopIdByUsername(String username);
}

