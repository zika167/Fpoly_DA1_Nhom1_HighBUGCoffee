/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Frame;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import lombok.Setter;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.entity.Bill;
import poly.cafe.entity.BillDetail;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.text.SimpleDateFormat;
/**
 *
 * @author Admin
 */
public class QRpaymentJDialog extends javax.swing.JDialog implements QRpaymentController {

    private Bill bill;
    private final BillDAO billDao = new BillDAOImpl();
    private final BillDetailDAO billDetailDao = new BillDetailDAOImpl();
    private List<BillDetail> billDetails = Collections.emptyList();
    
    @Setter
    private BillJDialog billJDialog; // Reference đến BillJDialog để đóng nó
    
    @Setter
    private SalesJDialog salesJDialog; // Reference đến SalesJDialog để mở nó

    public QRpaymentJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Thanh toán QR");
    }

    @Override
    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public void open() {
        if (bill == null || bill.getId() == null || bill.getId() <= 0) {
            XDialog.alert("Hóa đơn không hợp lệ hoặc không tồn tại!");
            this.dispose();
            return;
        }
        this.setLocationRelativeTo(this.getParent());
        this.fillBillDetails();
        this.setForm();
        this.setVisible(true);
    }

    @Override
    public void confirm() {
        if (bill == null || bill.getId() == null || bill.getId() <= 0) {
            XDialog.alert("Hóa đơn không hợp lệ!");
            return;
        }
        if (XDialog.confirm("Xác nhận thanh toán QR cho hóa đơn này?")) {
            try {
                bill.setStatus(Bill.Status.Completed.ordinal());
                bill.setCheckout(new java.util.Date());
                billDao.update(bill);
                XDialog.alert("Thanh toán QR thành công!");

                // Hiển thị màn hình cảm ơn (modal) trong 5s, sau đó quay lại SalesJDialog
                java.awt.Frame parentFrame = null;
                java.awt.Window owner = this.getOwner();
                if (owner instanceof java.awt.Frame) {
                    parentFrame = (java.awt.Frame) owner;
                }
                ThankJJDialog thank = new ThankJJDialog(parentFrame, true);
                thank.setLocationRelativeTo(parentFrame);
                // Hiển thị và chờ đến khi dialog đóng lại (modal)
                thank.setVisible(true);

                // Khi ThankJJDialog đóng, đóng BillJDialog và QRpayment, sau đó focus về SalesJDialog
                if (billJDialog != null) {
                    billJDialog.dispose();
                }
                // Đóng QRPayment
                this.dispose();
                // Quay lại SalesJDialog
                if (salesJDialog != null) {
                    salesJDialog.setVisible(true);
                    salesJDialog.toFront();
                    salesJDialog.requestFocus();
                    salesJDialog.loadCards();
                }
            } catch (Exception e) {
                XDialog.alert("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        this.dispose();
    }

    private void fillBillDetails() {
        if (bill == null || bill.getId() == null || bill.getId() <= 0) return;
        
        billDetails = billDetailDao.findByBillId(bill.getId());
        if (billDetails == null) {
            billDetails = Collections.emptyList();
        }
        
        DefaultTableModel model = (DefaultTableModel) tblTable.getModel();
        model.setRowCount(0);
        
        billDetails.forEach(d -> {
            if (d != null) {
                // TÍNH TOÁN BẰNG double
                double amount = d.getQuantity() * d.getUnitPrice() * (1 - d.getDiscount() / 100.0);

                Object[] row = {
                    d.getDrinkName() != null ? d.getDrinkName() : "N/A",
                    d.getQuantity(),
                    String.format("%.0f%%", d.getDiscount()),
                    String.format("%,.0f VNĐ", d.getUnitPrice()),
                    String.format("%,.0f VNĐ", amount)
                };
                model.addRow(row);
            }
        });
    }
    
    private void setForm() {
        if (bill == null) return;
    
    lblMaPhieu.setText(String.valueOf(bill.getId()));
    lblThoiGian.setText(bill.getCheckin() != null ? XDate.format(bill.getCheckin(), "HH:mm:ss dd-MM-yyyy") : "Chưa có");

    // TÍNH TỔNG TIỀN BẰNG double
    double total = billDetails.stream()
            .mapToDouble(d -> d.getQuantity() * d.getUnitPrice() * (1 - d.getDiscount() / 100.0))
            .sum();

    // ĐỊNH DẠNG TỔNG TIỀN THEO VNĐ
    lblTongCong.setText(String.format("%,.0f VNĐ", total));
    
    // Tạo nội dung QR đầy đủ
    StringBuilder qrContent = new StringBuilder();
    qrContent.append("Số hóa đơn: ").append(bill.getId()).append("\n");
    qrContent.append("Ngày in: ").append(new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new java.util.Date())).append("\n");
    qrContent.append("Người in: ").append(bill.getUsername() != null ? bill.getUsername() : "Không xác định").append("\n");
    qrContent.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", total)).append("\n");
    qrContent.append("Chi tiết sản phẩm:\n");
    
    for (BillDetail d : billDetails) {
        if (d != null) {
            qrContent.append("- ").append(d.getDrinkName() != null ? d.getDrinkName() : "N/A")
                     .append(": SL ").append(d.getQuantity())
                     .append(", Giá: ").append(String.format("%,.0f VNĐ", d.getUnitPrice()))
                     .append(", Giảm: ").append(String.format("%.0f%%", d.getDiscount()))
                     .append("\n");
        }
    }
    
    // Tạo và hiển thị mã QR
    try {
        BufferedImage qrImage = generateQRCode(qrContent.toString(), lblQR.getWidth(), lblQR.getHeight());
        lblQR.setIcon(new ImageIcon(qrImage));
        lblQR.setText("");
    } catch (Exception e) {
        lblQR.setText("Lỗi tạo QR");
        XDialog.alert("Không thể tạo mã QR: " + e.getMessage());
    }
    }

    private BufferedImage generateQRCode(String content, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblQR = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        lblMaPhieu = new javax.swing.JLabel();
        lblThoiGian = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTable = new javax.swing.JTable();
        lblTongCong = new javax.swing.JLabel();

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HighBUG");
        jLabel1.setOpaque(true);

        btnBack.setBackground(new java.awt.Color(122, 92, 62));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Quay lại");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblQR.setBackground(new java.awt.Color(204, 204, 204));
        lblQR.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblQR.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQR.setText("QR");
        lblQR.setOpaque(true);

        btnConfirm.setBackground(new java.awt.Color(122, 92, 62));
        btnConfirm.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConfirm.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirm.setText("Xác nhận");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        lblMaPhieu.setBackground(new java.awt.Color(255, 255, 255));
        lblMaPhieu.setOpaque(true);

        lblThoiGian.setBackground(new java.awt.Color(255, 255, 255));
        lblThoiGian.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Mã phiếu:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Tổng cộng:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Thời gian: ");

        tblTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Đồ uống", "Số lượng", "Giảm giá", "Đơn giá", "Thành tiền"
            }
        ));
        jScrollPane1.setViewportView(tblTable);

        lblTongCong.setBackground(new java.awt.Color(255, 255, 255));
        lblTongCong.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(15, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblMaPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(13, 13, 13)
                                    .addComponent(lblQR, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTongCong, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(btnBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(lblMaPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(lblThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblQR, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTongCong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        this.confirm();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        this.close();
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QRpaymentJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QRpaymentJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QRpaymentJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QRpaymentJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QRpaymentJDialog dialog = new QRpaymentJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblMaPhieu;
    private javax.swing.JLabel lblQR;
    private javax.swing.JLabel lblThoiGian;
    private javax.swing.JLabel lblTongCong;
    private javax.swing.JTable tblTable;
    // End of variables declaration//GEN-END:variables
 
}
