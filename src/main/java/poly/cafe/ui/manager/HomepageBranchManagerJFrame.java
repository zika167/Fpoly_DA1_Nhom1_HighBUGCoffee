/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package poly.cafe.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import poly.cafe.dao.RevenueDAO;
import poly.cafe.dao.TotalRevenueDAO;
import poly.cafe.dao.impl.RevenueDAOImpl;
import poly.cafe.dao.impl.TotalRevenueDAOImpl;
import poly.cafe.entity.Revenue;
import poly.cafe.entity.RevenueReportItem;
import poly.cafe.util.XAuth;
import poly.cafe.util.XJdbc;
// no direct JDBC here; reuse DAOs only

/**
 *
 * @author Zikar
 */
public class HomepageBranchManagerJFrame extends javax.swing.JFrame {

    private JPanel chartsGridPanel;
    private JPanel pnlChart1;
    private JPanel pnlChart2;
    private final RevenueDAO revenueDAO = new RevenueDAOImpl();
    private final TotalRevenueDAO totalRevenueDAO = new TotalRevenueDAOImpl();
    private static final int SIDEBAR_WIDTH = 220;

    /**
     * Creates new form HomepageBranchManagerJFrame
     */
    public HomepageBranchManagerJFrame() {
        initComponents();
        initFrame();
        buildChartsLayout();
        addChartsToPanel();
    }

    private void initFrame() {
        // Đặt giữa màn hình
        this.setLocationRelativeTo(null);

        // Đảm bảo hiển thị lên trên khi mở ra, sau đó trả về bình thường
        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        SwingUtilities.invokeLater(() -> this.setAlwaysOnTop(false));

        // Gán logo giống PolyCafe
        try {
            ImageIcon logoIcon = poly.cafe.util.IconUtils.loadCoffeeIcon(24, 24);
            if (logoIcon != null) {
                lbLogoName.setIcon(logoIcon);
                lbLogoName.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
                lbLogoName.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
                lbLogoName.setIconTextGap(10);
            }
        } catch (Exception ignore) {
        }

        // Khóa độ rộng sidebar (label + các nút) để không bị kéo giãn
        lockSidebarWidth();
    }

    private void buildChartsLayout() {
        try {
            // Sử dụng hai panel có sẵn trong jPanel5 do GUI builder tạo: jPanel3 và jPanel4
            Color bg = jPanel5.getBackground();

            // Dùng jPanel3 làm vùng chứa biểu đồ 1, jPanel4 cho biểu đồ 2
            pnlChart1 = jPanel3;
            pnlChart2 = jPanel4;

            jPanel3.setOpaque(true);
            jPanel3.setBackground(bg);
            jPanel3.removeAll();
            jPanel3.setLayout(new BorderLayout());

            jPanel4.setOpaque(true);
            jPanel4.setBackground(bg);
            jPanel4.removeAll();
            jPanel4.setLayout(new BorderLayout());

            jPanel3.revalidate();
            jPanel4.revalidate();
            jPanel3.repaint();
            jPanel4.repaint();
        } catch (Throwable t) {
            System.err.println("Không thể dựng layout biểu đồ: " + t.getMessage());
        }
    }

    // Vẽ 2 biểu đồ (cột + đường) vào hai panel con
    private void addChartsToPanel() {
        try {
            if (chartsGridPanel == null) {
                buildChartsLayout();
            }

            // 1) Bar chart: Top đồ uống theo doanh thu (DỮ LIỆU MẪU) -> pnlChart1
            DefaultCategoryDataset topDrinkDataset = new DefaultCategoryDataset();
            topDrinkDataset.addValue(2, "Doanh thu", "Cà phê đen");
            topDrinkDataset.addValue(1.5, "Doanh thu", "Cà phê sữa");
            topDrinkDataset.addValue(1.8, "Doanh thu", "Trà sữa");
            topDrinkDataset.addValue(1, "Doanh thu", "Nước cam");
            topDrinkDataset.addValue(0.8, "Doanh thu", "Soda chanh");
            JFreeChart topDrinkChart = ChartFactory.createBarChart(
                    "Top đồ uống theo doanh thu", "Đồ uống", "VNĐ", topDrinkDataset,
                    PlotOrientation.VERTICAL, false, true, false);
            setChartInPanel(pnlChart1, topDrinkChart);

            // 2) Line chart: Doanh thu theo tháng (DỮ LIỆU MẪU) -> pnlChart2
            DefaultCategoryDataset monthlyDataset = new DefaultCategoryDataset();
            int[] months = { 1, 2, 3, 4, 5, 6, 7 };
            double[] vals = { 2, 1.5, 1.8, 1, 0.8, 2.5, 3 };
            for (int i = 0; i < months.length; i++) {
                monthlyDataset.addValue(vals[i], "Doanh thu", "" + months[i]);
            }
            for (int m = 8; m <= 12; m++) {
                monthlyDataset.addValue(0, "Doanh thu", m + "");
            }
            JFreeChart monthlyChart = ChartFactory.createLineChart(
                    "Doanh thu theo tháng", "Tháng", "VNĐ", monthlyDataset,
                    PlotOrientation.VERTICAL, false, true, false);
            setChartInPanel(pnlChart2, monthlyChart);

            jPanel3.revalidate();
            jPanel4.revalidate();
            jPanel3.repaint();
            jPanel4.repaint();
        } catch (Throwable t) {
            System.err.println("Không thể tạo chart: " + t.getMessage());
        }
    }

    private ChartPanel wrapChart(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        // Thu nhỏ preferred/min size để không đẩy panel còn lại ra khỏi màn hình
        panel.setPreferredSize(new java.awt.Dimension(100, 100));
        panel.setMinimumSize(new java.awt.Dimension(0, 0));
        panel.setMouseWheelEnabled(true);
        // Cho phép chart vẽ ở kích thước rất lớn/nhỏ để không bị giới hạn
        panel.setMinimumDrawWidth(0);
        panel.setMinimumDrawHeight(0);
        panel.setMaximumDrawWidth(Integer.MAX_VALUE);
        panel.setMaximumDrawHeight(Integer.MAX_VALUE);
        return panel;
    }

    private void setChartInPanel(JPanel host, JFreeChart chart) {
        if (host == null)
            return;
        host.removeAll();
        host.add(wrapChart(chart), BorderLayout.CENTER);
        host.revalidate();
        host.repaint();
    }

    private void lockSidebarWidth() {
        java.awt.Dimension fixed = new java.awt.Dimension(SIDEBAR_WIDTH, 40);
        java.awt.Dimension fixedLabel = new java.awt.Dimension(SIDEBAR_WIDTH, 40);

        lbLogoName.setMinimumSize(fixedLabel);
        lbLogoName.setPreferredSize(fixedLabel);
        lbLogoName.setMaximumSize(new java.awt.Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));

        javax.swing.AbstractButton[] buttons = new javax.swing.AbstractButton[] {
                btnBranchManager, btnEmployeeBranchManager, btnCreateNewBranch, btnCreateNewBranchManager, btnExit };
        for (javax.swing.AbstractButton b : buttons) {
            b.setMinimumSize(fixed);
            b.setPreferredSize(fixed);
            b.setMaximumSize(new java.awt.Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        }
    }

    private String getCurrentShopId() {
        try {
            if (XAuth.user != null && XAuth.user.getShopId() != null) {
                return XAuth.user.getShopId();
            }
        } catch (Throwable ignore) {
        }
        // Fallback nếu chưa đăng nhập: dùng S01
        return "S01";
    }

    private Date[] getDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        Date end = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date begin = cal.getTime();
        return new Date[] { begin, end };
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private Map<Integer, Double> fetchMonthlyRevenue(String shopId, int year) {
        Map<Integer, Double> result = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MONTH(b.Checkout) AS monthNum, ");
        sql.append("       SUM(bd.Quantity * d.UnitPrice * (1 - d.Discount/100.0)) AS revenue ");
        sql.append("FROM BillDetails bd ");
        sql.append("JOIN Drinks d ON bd.DrinkId = d.Id ");
        sql.append("JOIN Bills b ON bd.BillId = b.Id ");
        sql.append("JOIN Users u ON b.Username = u.Username ");
        sql.append("WHERE b.Status = 1 AND b.Checkout IS NOT NULL AND YEAR(b.Checkout) = ? ");
        if (shopId != null && !shopId.isBlank()) {
            sql.append("AND u.ShopId = ? ");
        }
        sql.append("GROUP BY MONTH(b.Checkout) ");
        sql.append("ORDER BY monthNum");
        java.sql.ResultSet rs = null;
        try {
            if (shopId != null && !shopId.isBlank()) {
                rs = XJdbc.executeQuery(sql.toString(), year, shopId);
            } else {
                rs = XJdbc.executeQuery(sql.toString(), year);
            }
            while (rs.next()) {
                int month = rs.getInt("monthNum");
                double revenue = rs.getDouble("revenue");
                if (!Double.isFinite(revenue))
                    revenue = 0.0;
                result.put(month, revenue);
            }
        } catch (Exception ex) {
            System.err.println("Lỗi truy vấn doanh thu theo tháng: " + ex.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lbLogoName = new javax.swing.JLabel();
        btnBranchManager = new javax.swing.JButton();
        btnEmployeeBranchManager = new javax.swing.JButton();
        btnCreateNewBranch = new javax.swing.JButton();
        btnCreateNewBranchManager = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(245, 236, 213));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Trang chủ");

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));
        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator1)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 440,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 440,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addContainerGap(410, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(415, 415, 415)));
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 424,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 424,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(20, Short.MAX_VALUE)));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        lbLogoName.setBackground(new java.awt.Color(255, 255, 255));
        lbLogoName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbLogoName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbLogoName.setText("HighBUG");
        lbLogoName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 51, 0)));
        lbLogoName.setOpaque(true);

        btnBranchManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBranchManager.setText("QUẢN LÝ CHI NHÁNH");
        btnBranchManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBranchManagerActionPerformed(evt);
            }
        });

        btnEmployeeBranchManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnEmployeeBranchManager.setText("QUẢN LÝ NHÂN VIÊN");
        btnEmployeeBranchManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeBranchManagerActionPerformed(evt);
            }
        });

        btnCreateNewBranch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCreateNewBranch.setText("TẠO CHI NHÁNH MỚI");
        btnCreateNewBranch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewBranchActionPerformed(evt);
            }
        });

        btnCreateNewBranchManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCreateNewBranchManager.setText("TẠO QUẢN LÝ MỚI");
        btnCreateNewBranchManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewBranchManagerActionPerformed(evt);
            }
        });

        btnExit.setBackground(new java.awt.Color(113, 92, 62));
        btnExit.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("ĐĂNG XUẤT");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lbLogoName, javax.swing.GroupLayout.PREFERRED_SIZE, 176,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE, 176,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEmployeeBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCreateNewBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 176,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCreateNewBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 180,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(15, Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lbLogoName, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(btnBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnEmployeeBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnCreateNewBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnCreateNewBranchManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223,
                                        Short.MAX_VALUE)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnExitActionPerformed
        if (poly.cafe.util.XDialog.confirm("Bạn muốn đăng xuất?")) {
            try {
                this.dispose();
            } finally {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    poly.cafe.ui.LoginJDialog login = new poly.cafe.ui.LoginJDialog(null, true);
                    login.setLocationRelativeTo(null);
                    login.setVisible(true);
                });
            }
        }
    }// GEN-LAST:event_btnExitActionPerformed

    private void btnBranchManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBranchManagerActionPerformed
        BranchRevenueManagerJDialog dialog = new BranchRevenueManagerJDialog(this, true);
        dialog.setLocationRelativeTo(this);
        this.dispose(); // Đóng HomepageBranchManagerJFrame
        dialog.setVisible(true);
    }// GEN-LAST:event_btnBranchManagerActionPerformed

    private void btnEmployeeBranchManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnEmployeeBranchManagerActionPerformed
        EmployeeBranchManagerJDialog dialog = new EmployeeBranchManagerJDialog(this, true);
        dialog.setLocationRelativeTo(this);
        this.dispose(); // Đóng HomepageBranchManagerJFrame
        dialog.setVisible(true);
    }// GEN-LAST:event_btnEmployeeBranchManagerActionPerformed

    private void btnCreateNewBranchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateNewBranchActionPerformed
        NewBranchManagerJDialog dialog = new NewBranchManagerJDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }// GEN-LAST:event_btnCreateNewBranchActionPerformed

    private void btnCreateNewBranchManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateNewBranchManagerActionPerformed
        NewEmployeeBranchManagerJDialog dialog = new NewEmployeeBranchManagerJDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }// GEN-LAST:event_btnCreateNewBranchManagerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomepageBranchManagerJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomepageBranchManagerJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomepageBranchManagerJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomepageBranchManagerJFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomepageBranchManagerJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBranchManager;
    private javax.swing.JButton btnCreateNewBranch;
    private javax.swing.JButton btnCreateNewBranchManager;
    private javax.swing.JButton btnEmployeeBranchManager;
    private javax.swing.JButton btnExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbLogoName;
    // End of variables declaration//GEN-END:variables
}
