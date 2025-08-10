/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.manager;

/**
 *
 * @author User
 */
public interface BranchRevenueController {
     /**
     * Khởi tạo các thành phần và dữ liệu ban đầu cho dialog.
     */
    void init();
    
    /**
     * Tải danh sách các chi nhánh lên bảng.
     */
    void fillBranchTable();
    
    /**
     * Tải báo cáo doanh thu chi tiết cho chi nhánh được chọn.
     */
    void fillRevenueReport();
    
    /**
     * Cập nhật thông tin tóm tắt của chi nhánh được chọn.
     */
    void updateSummaryInfo();
    
    /**
     * Xử lý sự kiện khi người dùng chọn một khoảng thời gian định sẵn.
     */
    void selectPredefinedTimeRange();
}

