/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

/**
 *
 * @author LENOVO
 */
public class Employee {
    private String username;
    private String password;
    private String fullname;
    private String phone;
    private boolean active;

    // Constructors, getters, setters
    public Employee() {}
    public Employee(String username, String password, String fullname, String phone, boolean active) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.phone = phone;
        this.active = active;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
