/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package poly.cafe.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private Long id;
    private String username;
    private Integer cardId;

    // Đảm bảo hai trường này là kiểu java.util.Date
    private Date checkin;
    private Date checkout;

    private Integer status;
    private Long paymentId;

    // Thêm enum Status để quản lý trạng thái tốt hơn (tùy chọn nhưng khuyến nghị)
    public enum Status {
        Servicing, Completed, Canceled
    }
}
