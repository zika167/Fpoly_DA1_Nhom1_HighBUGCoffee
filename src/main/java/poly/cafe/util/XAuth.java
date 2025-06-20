/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.util;

import poly.cafe.entity.User;

/**
 *
 * @author wangquockhanh
 */
public class XAuth {
    public static User user = User.builder()
            .username("user1@gmail.com")
            .password("123")
            .enabled(true)
            .fullname("Nguyễn Văn Tèo")
            .photo("hinh2.jpg")
            .manager(true)
            .build(); // biến user này sẽ được thay thế sau khi đăng nhập

}
