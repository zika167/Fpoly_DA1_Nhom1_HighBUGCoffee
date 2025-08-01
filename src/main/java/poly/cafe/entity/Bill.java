/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class Bill {
    public enum Status {
        Servicing,
        Completed,
        Canceled
    }

    private Long id;
    private String username;
    private Integer cardId;

    @Builder.Default
    private Date checkin = new Date();

    private Date checkout;
    private int status;
    private Long paymentId;
    private String shopId;
}

//public class Bill {
//    public enum Status {    
//        Servicing,          // 0
//        Completed,          // 1
//        Canceled            // 2
//    }
//    private Long id;
//    private String username;
//    private Integer cardId;
//    private Date checkin = new Date();
//    private Date checkout;
//    private int status;
//    
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
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
//    public Integer getCardId() {
//        return cardId;
//    }
//
//    public void setCardId(Integer cardId) {
//        this.cardId = cardId;
//    }
//
//    public Date getCheckin() {
//        return checkin;
//    }
//
//    public void setCheckin(Date checkin) {
//        this.checkin = checkin;
//    }
//
//    public Date getCheckout() {
//        return checkout;
//    }
//
//    public void setCheckout(Date checkout) {
//        this.checkout = checkout;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public Bill(Long id, String username, Integer cardId, Date checkin, Date checkout, int status) {
//        this.id = id;
//        this.username = username;
//        this.cardId = cardId;
//        this.checkin = checkin;
//        this.checkout = checkout;
//        this.status = status;
//    }
//
//    public Bill() {
//    }
//    
//    // Builder nội bộ
//    public static class Builder {
//        private final Bill bill;
//
//        public Builder() {
//            bill = new Bill();
//        }
//
//        public Builder id(Long id) {
//            bill.setId(id);
//            return this;
//        }
//
//        public Builder username(String username) {
//            bill.setUsername(username);
//            return this;
//        }
//
//        public Builder cardId(Integer cardId) {
//            bill.setCardId(cardId);
//            return this;
//        }
//
//        public Builder checkin(Date checkin) {
//            bill.setCheckin(checkin);
//            return this;
//        }
//
//        public Builder checkout(Date checkout) {
//            bill.setCheckout(checkout);
//            return this;
//        }
//
//        public Builder status(int status) {
//            bill.setStatus(status);
//            return this;
//        }
//
//        public Bill build() {
//            return bill;
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//}

