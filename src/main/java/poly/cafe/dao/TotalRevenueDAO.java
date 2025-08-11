/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

/*
 *
 * @author User
 */
import java.util.Date;
import java.util.List;
import poly.cafe.entity.RevenueReportItem;

public interface TotalRevenueDAO {
     /**
     * Lấy danh sách thống kê doanh thu chi tiết theo từng món đồ uống
     * của một chi nhánh trong một khoảng thời gian.
     * @param shopId Mã chi nhánh cần thống kê.
     * @param begin Ngày bắt đầu.
     * @param end Ngày kết thúc.
     * @return Danh sách các món đồ uống đã bán và doanh thu của chúng.
     */
    List<RevenueReportItem> getRevenueByShopAndDateRange(String shopId, Date begin, Date end);
}
