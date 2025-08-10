/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.Date;
import java.util.List;
import poly.cafe.dao.RevenueDAO;
import poly.cafe.entity.Revenue.ByCategory;
import poly.cafe.entity.Revenue.ByUser;
import poly.cafe.util.XQuery;

/**
 *
 * @author wangquockhanh
 */
public class RevenueDAOImpl implements RevenueDAO {

    @Override
    public List<ByCategory> getByCategory(Date begin, Date end, String shopId) {
        String revenueByCategorySql = "SELECT category.Name AS `Category`, "
                + "   drink.Name AS DrinkName,"
                + "   sum(detail.Quantity) AS Quantity,"
                + "   sum(drink.UnitPrice*detail.Quantity*(1-drink.Discount/100)) AS Revenue "
                + "FROM BillDetails detail "
                + "   JOIN Drinks drink ON drink.Id=detail.DrinkId"
                + "   JOIN Categories category ON category.Id=drink.CategoryId"
                + "   JOIN Bills bill ON bill.Id=detail.BillId "
                + "   JOIN Users u ON u.Username = bill.Username "
                + "WHERE bill.Status = 1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "   AND u.ShopId = ? "
                + "GROUP BY category.Name, drink.Name "
                + "ORDER BY category.Name, Revenue DESC";
        return XQuery.getBeanList(ByCategory.class, revenueByCategorySql, begin, end, shopId);
    }

    @Override
    public List<ByUser> getByUser(Date begin, Date end, String shopId) {
        String revenueByUserSql = "SELECT bill.Username AS `User`, "
                + "   user.FullName AS EmployeeName,"
                + "   count(DISTINCT detail.BillId) AS Quantity,"
                + "   sum(drink.UnitPrice*detail.Quantity*(1-drink.Discount/100)) AS Revenue "
                + "FROM BillDetails detail "
                + "   JOIN Drinks drink ON drink.Id=detail.DrinkId "
                + "   JOIN Bills bill ON bill.Id=detail.BillId "
                + "   JOIN Users user ON user.Username = bill.Username "
                + "WHERE bill.Status=1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "   AND user.ShopId = ? "
                + "GROUP BY bill.Username, user.FullName "
                + "ORDER BY Revenue DESC";
        return XQuery.getBeanList(ByUser.class, revenueByUserSql, begin, end, shopId);
    }
}
