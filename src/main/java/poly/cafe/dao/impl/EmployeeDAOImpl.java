/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.EmployeeDAO;
import poly.cafe.entity.Employee;
/**
 *
 * @author LENOVO
 */
public class EmployeeDAOImpl implements EmployeeDAO {
    private List<Employee> employees = new ArrayList<>(); // Thay bằng kết nối DB thực tế

    @Override
    public Employee create(Employee employee) {
        employees.add(employee);
        return employee;
    }

    @Override
    public boolean update(Employee employee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getUsername().equals(employee.getUsername())) {
                employees.set(i, employee);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String username) {
        return employees.removeIf(e -> e.getUsername().equals(username));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    @Override
    public Employee findByFullname(String fullname) {
        return employees.stream().filter(e -> e.getFullname().equals(fullname)).findFirst().orElse(null);
    }
}
