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

    // Định nghĩa Enum cho các vai trò
    public enum Role {
        staff,
        branch_manager,
        chain_manager
    }

    private String username;
    private String password;
    private boolean enabled;
    private String fullname;
    private String phone; // Thêm trường số điện thoại

    @Builder.Default
    private String photo = "hinh2.jpg";

    private Role role; // Sử dụng Enum thay cho boolean
    private String shopId;

    // (Tùy chọn) Thêm các phương thức kiểm tra vai trò cho tiện lợi
    public boolean isChainManager() {
        return this.role == Role.chain_manager;
    }

    public boolean isBranchManager() {
        return this.role == Role.branch_manager;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
