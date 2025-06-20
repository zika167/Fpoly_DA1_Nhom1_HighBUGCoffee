/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.manager;

import poly.cafe.entity.Bill;
import poly.cafe.ui.CrudController;

/**
 *
 * @author wangquockhanh
 */
public interface BillManagerController extends CrudController<Bill> {
    void fillBillDetails();
    void selectTimeRange();
}
