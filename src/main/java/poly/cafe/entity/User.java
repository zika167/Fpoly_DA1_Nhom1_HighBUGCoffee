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
/*public class User {
    private String username;
    private String password;
    private boolean enabled;
    private String fullname;
    
    @Builder.Default
    private String photo = "hinh2.jpg";  // giá trị mặc định
    
    private boolean manager;
}*/
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
}

//public class User {
//    private String username;
//    private String password;
//    private boolean enabled;
//    private String fullname;
//    private String photo = "hinh2.jpg";
//    private boolean manager;
//
//    public User(String username, String password, boolean enabled, String fullname, String photo, boolean manager) {
//        this.username = username;
//        this.password = password;
//        this.enabled = enabled;
//        this.fullname = fullname;
//        this.photo = photo;
//        this.manager = manager;
//    }
//
//    public User() {
//    }
//    
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public String getFullname() {
//        return fullname;
//    }
//
//    public void setFullname(String fullname) {
//        this.fullname = fullname;
//    }
//
//    public String getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(String photo) {
//        this.photo = photo;
//    }
//
//    public boolean isManager() {
//        return manager;
//    }
//
//    public void setManager(boolean manager) {
//        this.manager = manager;
//    }
//    
//    public static class Builder {
//        private final User user;
//
//        public Builder() {
//            user = new User();
//        }
//
//        public Builder username(String username) {
//            user.setUsername(username);
//            return this;
//        }
//
//        public Builder password(String password) {
//            user.setPassword(password);
//            return this;
//        }
//
//        public Builder enabled(boolean enabled) {
//            user.setEnabled(enabled);
//            return this;
//        }
//
//        public Builder fullname(String fullname) {
//            user.setFullname(fullname);
//            return this;
//        }
//
//        public Builder photo(String photo) {
//            user.setPhoto(photo);
//            return this;
//        }
//
//        public Builder manager(boolean manager) {
//            user.setManager(manager);
//            return this;
//        }
//
//        public User build() {
//            return user;
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//    
//    @Override
//    public String toString() {
//        return "User{" +
//               "username='" + username + '\'' +
//               ", password='" + password + '\'' +
//               ", enabled=" + enabled +
//               ", fullname='" + fullname + '\'' +
//               ", photo='" + photo + '\'' +
//               ", manager=" + manager +
//               '}';
//    }
//}
