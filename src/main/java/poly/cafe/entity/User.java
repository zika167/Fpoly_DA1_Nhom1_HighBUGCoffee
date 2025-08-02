/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String username;
    private String password;
    private boolean enabled;
    private String fullname;   
    @Builder.Default
    private String photo = "hinh2.jpg";  // giá trị mặc định    
    private boolean manager;
    private String phoneNumber;
    private String role;
    private int id;
    private String email;
    
    public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}

public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}

public String getPhoneNumber() {
    return phoneNumber;
}
public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
}

public String getRole() {
    return role;
}
public void setRole(String role) {
    this.role = role;
}

}


