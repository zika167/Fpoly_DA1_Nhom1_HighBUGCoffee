/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Frame;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import lombok.Setter;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.entity.Bill;
import poly.cafe.entity.BillDetail;
import poly.cafe.ui.BillController;
import poly.cafe.ui.DrinkJDialog;
import poly.cafe.ui.QRpaymentJDialog;
import poly.cafe.ui.SalesJDialog;
import poly.cafe.ui.ThankJJDialog;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;
import poly.cafe.util.XQuery;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author wangquockhanh
 */
public class BillJDialog extends javax.swing.JDialog implements BillController {

    /**
     * Creates new form BillJDialog1
     */
    public BillJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @Setter
    Bill bill; // bill được truyền từ bên ngoài vào

    BillDAO billDao = new BillDAOImpl();
    BillDetailDAO billDetailDao = new BillDetailDAOImpl();
    List<BillDetail> billDetails = List.of();

    /*
     * @Override
     * public void removeDrinks() { // xóa đồ uống được tích chọn
     * if (bill == null || bill.getId() == null) {
     * XDialog.alert("Không thể xóa đồ uống khi chưa có phiếu bán hàng!");
     * return;
     * }
     * 
     * for (int i = 0; i < tblBillDetails.getRowCount(); i++) {
     * Boolean checked = (Boolean) tblBillDetails.getValueAt(i, 0);
     * if (checked && i < billDetails.size())
     * billDetailDao.deleteById(billDetails.get(i).getId());
     * }
     * this.fillBillDetails();
     * }
     */
    @Override
    public void removeDrinks() { // xóa đồ uống được tích chọn
        // ... (phần kiểm tra bill giữ nguyên)

        // Lấy ID trực tiếp từ bảng để xóa, tránh lỗi khi sắp xếp
        for (int i = 0; i < tblBillDetails.getRowCount(); i++) {
            Boolean checked = (Boolean) tblBillDetails.getValueAt(i, 0);
            if (checked != null && checked) {
                // Lấy ID từ cột thứ 2 (cột "Mã phiếu", index = 1)
                Long billDetailId = (Long) tblBillDetails.getValueAt(i, 1);
                billDetailDao.deleteById(billDetailId);
            }
        }
        this.fillBillDetails(); // Tải lại dữ liệu
    }

    @Override
    public void showDrinkJDialog() { // hiển thị cửa sổ chọn và bổ sung đồ uống
        if (bill == null || bill.getId() == null) {
            XDialog.alert("Không thể thêm đồ uống khi chưa có phiếu bán hàng!");
            return;
        }

        DrinkJDialog dialog = new DrinkJDialog((Frame) this.getOwner(), true);
        dialog.setBill(bill); // Khai báo vào DrinkJDialog @Setter Bill bill
        dialog.setVisible(true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                BillJDialog.this.fillBillDetails();
            }
        });
    }

    /*
     * @Override
     * public void updateQuantity() { // thay đổi số lượng đồ uống
     * int row = tblBillDetails.getSelectedRow();
     * if (row == -1)
     * return; // chưa chọn
     * 
     * if (bill != null && bill.getStatus() == 0) {
     * String input = XDialog.prompt("Số lượng mới?");
     * if (input != null && !input.isBlank()) {
     * BillDetail detail = billDetails.get(row);
     * detail.setQuantity(Integer.parseInt(input));
     * billDetailDao.update(detail);
     * this.fillBillDetails();
     * }
     * }
     * }
     */
    @Override
    public void updateQuantity() { // thay đổi số lượng đồ uống
        int row = tblBillDetails.getSelectedRow();
        if (row == -1) {
            return;
        }

        if (bill != null && bill.getStatus() == 0) {
            String input = XDialog.prompt("Số lượng mới?");
            if (input != null && !input.isBlank()) {
                try {
                    int newQuantity = Integer.parseInt(input);
                    if (newQuantity <= 0) {
                        XDialog.alert("Số lượng phải lớn hơn 0.");
                        return;
                    }

                    // Lấy ID trực tiếp từ bảng
                    Long billDetailId = (Long) tblBillDetails.getValueAt(row, 1);

                    // Tìm đúng đối tượng BillDetail để cập nhật
                    BillDetail detailToUpdate = billDetails.stream()
                            .filter(d -> d.getId().equals(billDetailId))
                            .findFirst()
                            .orElse(null);

                    if (detailToUpdate != null) {
                        detailToUpdate.setQuantity(newQuantity);
                        billDetailDao.update(detailToUpdate);
                        this.fillBillDetails(); // Tải lại
                    }
                } catch (NumberFormatException e) {
                    XDialog.alert("Vui lòng nhập một số hợp lệ!");
                }
            }
        }
    }

    @Override
    public void checkout() {
        if (bill == null || bill.getId() == null) {
            XDialog.alert("Không thể thanh toán khi chưa có phiếu bán hàng!");
            return;
        }

        if (XDialog.confirm("Bạn muốn thanh toán phiếu bán hàng?")) {
            bill.setStatus(Bill.Status.Completed.ordinal());
            bill.setCheckout(new Date());
            billDao.update(bill);
            this.setForm(bill); // Cập nhật form BillJDialog

            // Ẩn BillJDialog tạm thời để hiển thị Thank
            this.setVisible(false);

            // Mở ThankJJDialog
            ThankJJDialog thankDialog = new ThankJJDialog((Frame) this.getOwner(), true);
            thankDialog.setVisible(true);

            // Tự động đóng Thank sau 5 giây
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    thankDialog.dispose(); // Đóng Thank
                }
            }, 5000); // 5000 ms = 5 giây

            // Sau khi Thank đóng (dù tự động hay manual), quay lại BillJDialog
            thankDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    // Hiển thị lại BillJDialog
                    BillJDialog.this.setVisible(true);

                    // Optional: Reset bill hoặc tải lại dữ liệu nếu cần (ví dụ: clear bill để tạo
                    // mới)
                    // bill = null;
                    // fillBillDetails();
                    // setForm(bill);

                    // Optional: Refresh SalesJDialog nếu vẫn muốn
                    if (getOwner() instanceof javax.swing.JFrame) {
                        java.awt.Window[] windows = getOwner().getOwnedWindows();
                        for (java.awt.Window window : windows) {
                            if (window instanceof SalesJDialog) {
                                ((SalesJDialog) window).loadCards();
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void cancel() {
        if (bill == null || bill.getId() == null) {
            XDialog.alert("Không thể hủy khi chưa có phiếu bán hàng!");
            return;
        }

        if (billDetails.isEmpty()) {
            // Nếu phiếu mới tạo và chưa có chi tiết nào, xóa luôn khỏi database
            if (XDialog.confirm("Phiếu bán hàng chưa có đồ uống nào. Bạn có muốn xóa phiếu này?")) {
                billDao.deleteById(bill.getId());
                this.dispose();
            }
        } else if (XDialog.confirm("Bạn muốn hủy phiếu bán hàng? (Các đồ uống đã chọn sẽ bị hủy)")) {
            bill.setStatus(Bill.Status.Canceled.ordinal());
            billDao.update(bill);
            this.setForm(bill);
        }
    }

    void setForm(Bill bill) { // hiển thị bill lên form
        if (bill == null) {
            XDialog.alert("Hóa đơn không hợp lệ!");
            return;
        }
        txtId.setText(String.valueOf(bill.getId()));
        txtId.setEditable(false); // Không cho phép chỉnh sửa mã phiếu
        txtCardId.setText("Card #" + (bill.getCardId() != null ? bill.getCardId() : ""));
        txtCardId.setEditable(false); // Không cho phép chỉnh sửa mã thẻ
        txtCheckin.setText(
                bill.getCheckin() != null ? XDate.format(bill.getCheckin(), "HH:mm:ss dd-MM-yyyy") : "Chưa có");
        txtCheckin.setEditable(false); // Không cho phép chỉnh sửa thời gian đặt hàng
        txtUsername.setText(bill.getUsername() != null ? bill.getUsername() : "");
        txtUsername.setEditable(false); // Không cho phép chỉnh sửa nhân viên
        String[] statuses = { "Servicing", "Completed", "Canceled" };
        txtStatus.setText(
                bill.getStatus() >= 0 && bill.getStatus() < statuses.length ? statuses[bill.getStatus()] : "Unknown");
        txtStatus.setEditable(false); // Không cho phép chỉnh sửa trạng thái
        txtCheckout.setText(bill.getCheckout() != null ? XDate.format(bill.getCheckout(), "HH:mm:ss dd-MM-yyyy") : "");
        txtCheckout.setEditable(false); // Không cho phép chỉnh sửa thời gian thanh toán
        boolean editable = (bill.getStatus() == 0);
        btnAdd.setEnabled(editable);
        btnCancel.setEnabled(editable);
        btnCheckout.setEnabled(editable);
        btnRemove.setEnabled(editable);
    }

    @Override
    public void open() {
        if (bill == null) {
            XDialog.alert("Vui lòng chọn hoặc tạo hóa đơn trước!");
            this.dispose();
            return;
        }
        this.setLocationRelativeTo(null);
        this.setForm(bill);
        this.fillBillDetails();
    }

    @Override
    public void close() {
        if (bill != null && bill.getId() != null && billDetails.isEmpty()) {
            // Tự động xóa phiếu mới tạo nếu chưa có chi tiết nào
            billDao.deleteById(bill.getId());
        }
    }

    /*
     * void fillBillDetails() {
     * if (bill == null || bill.getId() == null || bill.getId() == 0) {
     * ((DefaultTableModel) tblBillDetails.getModel()).setRowCount(0);
     * return;
     * }
     * billDetails = billDetailDao.findByBillId(bill.getId());
     * 
     * DefaultTableModel model = (DefaultTableModel) tblBillDetails.getModel();
     * model.setRowCount(0);
     * billDetails.forEach(d -> {
     * if (d != null) {
     * Double amt = d.getQuantity() * d.getUnitPrice() * (1 - d.getDiscount());
     * Object[] row = {
     * false,
     * d.getId(),
     * d.getDrinkName(),
     * String.format("%,.0f VNĐ", d.getUnitPrice()),
     * String.format("%.0f%%", d.getDiscount() * 100),
     * d.getQuantity(),
     * String.format("$%.2f", amt)
     * };
     * model.addRow(row);
     * }
     * });
     * }
     */
    void fillBillDetails() {
        // 1. Giữ lại phần kiểm tra an toàn để tránh lỗi NullPointerException
        if (bill == null || bill.getId() == null || bill.getId() <= 0) {
            // Nếu không có hóa đơn hợp lệ, chỉ cần xóa trắng bảng chi tiết
            ((DefaultTableModel) tblBillDetails.getModel()).setRowCount(0);
            return;
        }

        // Tải chi tiết hóa đơn từ CSDL
        billDetails = billDetailDao.findByBillId(bill.getId());

        DefaultTableModel model = (DefaultTableModel) tblBillDetails.getModel();
        model.setRowCount(0); // Xóa các dòng cũ

        billDetails.forEach(d -> {
            if (d != null) {
                // 2. Sử dụng công thức tính giảm giá đúng (chia cho 100)
                // Giả sử discount được lưu là số nguyên (ví dụ: 10 cho 10%)
                Double amt = d.getQuantity() * d.getUnitPrice() * (1 - d.getDiscount() / 100.0);

                Object[] row = {
                        false,
                        d.getId(),
                        d.getDrinkName(),
                        // 3. Định dạng tiền tệ nhất quán theo VNĐ
                        String.format("%,.0f VNĐ", d.getUnitPrice()),
                        // 4. Hiển thị giảm giá khớp với cách lưu trữ
                        String.format("%.0f%%", d.getDiscount()),
                        d.getQuantity(),
                        // 3. Định dạng tiền tệ nhất quán theo VNĐ
                        String.format("%,.0f VNĐ", amt)
                };
                model.addRow(row);
            }
        });
    }

    @Override
    public void setBill(Bill bill) {
        this.bill = bill;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtCheckout = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnRemove = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        txtId = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        txtCardId = new javax.swing.JTextField();
        btnCheckout = new javax.swing.JButton();
        txtCheckin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBillDetails = new javax.swing.JTable();
        txtStatus = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbbThanhToan = new javax.swing.JComboBox<>();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phiếu bán hàng");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        txtCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCheckoutActionPerformed(evt);
            }
        });

        jLabel2.setText("Thẻ số");

        btnRemove.setBackground(new java.awt.Color(122, 92, 62));
        btnRemove.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRemove.setForeground(new java.awt.Color(255, 255, 255));
        btnRemove.setText("Xóa đồ uống");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        jLabel3.setText("Thời điểm đặt hàng");

        btnAdd.setBackground(new java.awt.Color(122, 92, 62));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Thêm đồ uống");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnCancel.setBackground(new java.awt.Color(63, 195, 104));
        btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Hủy phiếu");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnCheckout.setBackground(new java.awt.Color(221, 64, 64));
        btnCheckout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckout.setText("Thanh toán");
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });

        jLabel4.setText("Nhân viên");

        jLabel5.setText("Trạng thái");

        tblBillDetails.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null }
                },
                new String[] {
                        "", "Mã phiếu", "Đồ uống", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"
                }) {
            Class[] types = new Class[] {
                    java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        tblBillDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBillDetailsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBillDetails);

        jLabel6.setText("Thời điểm thanh toán");

        jLabel1.setText("Mã phiếu");

        cbbThanhToan.setBackground(new java.awt.Color(238, 142, 41));
        cbbThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cbbThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        cbbThanhToan.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Chọn kiểu thanh toán", "Thanh toán tiền mặt", "Thanh toán QR", " ", " " }));
        cbbThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbThanhToanActionPerformed(evt);
            }
        });

        btnBack.setBackground(new java.awt.Color(122, 92, 62));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Quay lại");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout.createSequentialGroup()
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                189, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false)
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 595,
                                                                Short.MAX_VALUE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addGroup(jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(jLabel4,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                190, Short.MAX_VALUE)
                                                                                        .addComponent(txtUsername))
                                                                                .addGap(12, 12, 12)
                                                                                .addGroup(jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jLabel5,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                185,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(txtStatus,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                191,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addGroup(jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(jLabel1,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                190, Short.MAX_VALUE)
                                                                                        .addComponent(txtId))
                                                                                .addGap(12, 12, 12)
                                                                                .addGroup(jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jLabel2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                185,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(txtCardId,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                191,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                        .addComponent(jLabel6,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                190, Short.MAX_VALUE)
                                                                        .addComponent(txtCheckout,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                190, Short.MAX_VALUE)
                                                                        .addComponent(jLabel3,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                190, Short.MAX_VALUE)
                                                                        .addComponent(txtCheckin))))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 189,
                                                                Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 189,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                .createSequentialGroup()
                                                .addGap(201, 201, 201)
                                                .addComponent(cbbThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 189,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 189,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap()));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCardId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCheckin, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCheckout, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdd)
                                        .addComponent(btnCheckout)
                                        .addComponent(cbbThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnRemove)
                                        .addComponent(btnCancel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBack)
                                .addContainerGap(15, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed
        // Đóng dialog hiện tại
        this.dispose();

        // Focus lại vào SalesJDialog nếu có
        if (this.getOwner() != null) {
            this.getOwner().toFront();
            this.getOwner().requestFocus();
        }
    }// GEN-LAST:event_btnBackActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        this.removeDrinks();
    }// GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        this.showDrinkJDialog();
    }// GEN-LAST:event_btnAddActionPerformed

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCheckoutActionPerformed
        // TODO add your handling code here:
        if (bill == null || bill.getId() == null) {
            XDialog.alert("Không thể thanh toán khi chưa có phiếu bán hàng!");
            return;
        }

        String selected = (String) cbbThanhToan.getSelectedItem();
        if ("Thanh toán QR".equals(selected)) {
            QRpaymentJDialog qrDialog = new QRpaymentJDialog((Frame) this.getOwner(), true);
            qrDialog.setBill(bill);
            qrDialog.open();
            qrDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    BillJDialog.this.setForm(bill);
                }
            });
        } else if ("Thanh toán tiền mặt".equals(selected)) {
            this.checkout();
        } else {
            XDialog.alert("Vui lòng chọn phương thức thanh toán!");
        }
    }// GEN-LAST:event_btnCheckoutActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        this.cancel();
    }// GEN-LAST:event_btnCancelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }// GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        this.close();
    }// GEN-LAST:event_formWindowClosed

    private void tblBillDetailsMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tblBillDetailsMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            this.updateQuantity();
        }

    }// GEN-LAST:event_tblBillDetailsMouseClicked

    private void cbbThanhToanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbbThanhToanActionPerformed
        // TODO add your handling code here:
        /*
         * if (bill == null || bill.getId() == null) {
         * XDialog.alert("Không thể thanh toán khi chưa có phiếu bán hàng!");
         * return;
         * }
         * 
         * String selected = (String) cbbThanhToan.getSelectedItem();
         * if ("Thanh toán QR".equals(selected)) {
         * QRpaymentJDialog qrDialog = new QRpaymentJDialog((Frame) this.getOwner(),
         * true);
         * qrDialog.setBill(bill);
         * qrDialog.open();
         * qrDialog.addWindowListener(new java.awt.event.WindowAdapter() {
         * 
         * @Override
         * public void windowClosed(java.awt.event.WindowEvent e) {
         * BillJDialog.this.setForm(bill);
         * }
         * });
         * } else if ("Thanh toán tiền mặt".equals(selected)) {
         * this.checkout();
         * }
         */
    }// GEN-LAST:event_cbbThanhToanActionPerformed

    private void txtCheckoutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtCheckoutActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txtCheckoutActionPerformed

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
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillJDialog dialog = new BillJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox<String> cbbThanhToan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBillDetails;
    private javax.swing.JTextField txtCardId;
    private javax.swing.JTextField txtCheckin;
    private javax.swing.JTextField txtCheckout;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
