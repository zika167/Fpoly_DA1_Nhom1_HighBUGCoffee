/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import poly.cafe.entity.Bill;

/**
 *
 * @author LENOVO
 */
public interface QRpaymentController {
    void setBill(Bill bill); //thiết lập hóa đơn hiển thị trong hộp thoại
    void open(); //hiển thị hộp thoại với thông tin chi tiết về hóa đơn
    void confirm(); //xác nhận thanh toán   QR và cập nhật trạng thái hóa đơn
    void close(); //đóng hộp thoại
}
