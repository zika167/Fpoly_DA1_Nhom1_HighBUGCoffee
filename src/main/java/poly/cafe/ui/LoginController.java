/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import poly.cafe.util.XDialog;
import javax.swing.JDialog;
import javax.swing.JFrame;
/**
 *
 * @author wangquockhanh
 */
public interface LoginController {
    void open();
    void login();
    default void exit(){
        if(XDialog.confirm("Bạn muốn kết thúc?")){
            System.exit(0);
        }
    }
    
    default void showWelcomeJDialog(JFrame frame){
            this.showJDialog(new WelcomeJDialog(frame, true));
        }  

    default void showJDialog(JDialog dialog){
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}
