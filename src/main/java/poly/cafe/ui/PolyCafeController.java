/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import poly.cafe.ui.manager.CardManagerJDialog;
import javax.swing.JDialog;
import javax.swing.JFrame;
import poly.cafe.ui.manager.BillManagerJDialog;
import poly.cafe.ui.manager.CategoryManagerJDialog;
import poly.cafe.ui.manager.DrinkManagerJDialog;
import poly.cafe.ui.manager.RevenueManagerJDialog;
import poly.cafe.ui.manager.UserManagerJDialog;
import poly.cafe.util.XDialog;

/**
 *
 * @author wangquockhanh
 */
public interface PolyCafeController {
    /**
     * Hiển thị cửa sổ chào
     * Hiển thị cửa sổ đăng nhập
     * Hiển thị thông tin user đăng nhập
     * Disable/Enable các thành phần tùy thuộc vào vai trò đăng nhập
     */
    void init();
    
    default void exit(){
        if(XDialog.confirm("Bạn muốn kết thúc?")){
            System.exit(0);
        }
    }
    default void showJDialog(JDialog dialog){
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
//    default void showWelcomeJDialog(JFrame frame){
//        this.showJDialog(new WelcomeJDialog(frame, true));
//    }
//   
//    default void showLoginJDialog(JFrame frame){
//        this.showJDialog(new LoginJDialog(frame, true));
//    }
  
    default void showChangePasswordJDialog(JFrame frame){
        this.showJDialog(new ChangePasswordJDialog(frame, true));
    }
    
    default void showSalesJDialog(JFrame frame){
        this.showJDialog(new SalesJDialog(frame, true));
    }
    
    default void showHistoryJDialog(JFrame frame){
        this.showJDialog(new HistoryJDialog(frame, true));
    }
    
    default void showDrinkManagerJDialog(JFrame frame){
        this.showJDialog(new DrinkManagerJDialog(frame, true));
    }
    
    default void showCategoryManagerJDialog(JFrame frame){
        this.showJDialog(new CategoryManagerJDialog(frame, true));
    }
    
    default void showCardManagerJDialog(JFrame frame){
        this.showJDialog(new CardManagerJDialog(frame, true));
    }
    
    default void showBillManagerJDialog(JFrame frame){
        this.showJDialog(new BillManagerJDialog(frame, true));
    }
    
    default void showUserManagerJDialog(JFrame frame){
        this.showJDialog(new UserManagerJDialog(frame, true));
    }
    
    default void showRevenueManagerJDialog(JFrame frame){
        this.showJDialog(new RevenueManagerJDialog(frame, true));
    }


}
