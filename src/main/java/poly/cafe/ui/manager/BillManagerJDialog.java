/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.entity.BillDetail;
import poly.cafe.entity.Bill;
import poly.cafe.util.TimeRange;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;
import poly.cafe.util.XIcon;

/**
 *
 * @author wangquockhanh
 */
public class BillManagerJDialog extends javax.swing.JDialog implements BillManagerController {

    /**
     * Creates new form BillManagerJDialog
     */
    public BillManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initIcons();
    }

    BillDAO dao = new BillDAOImpl();
    List<Bill> items = List.of();
    BillDetailDAO billDetailDao = new BillDetailDAOImpl();
    List<BillDetail> details = List.of(); // chi tiết phiếu bán hàng
    // Khai báo SimpleDateFormat một lần duy nhất ở đây
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ENGLISH);

    private String getStatusName(int status) {
        return switch (status) {
            case 0 -> "Serciving";
            case 1 -> "Completed";
            case 2 -> "Canceled";
            default -> null;
        };
    }

    @Override
    public void fillBillDetails() {
        DefaultTableModel model = (DefaultTableModel) tblBillDetails.getModel();
        model.setRowCount(0);
        details = List.of();
        if (!txtId.getText().isBlank()) {
            Long billId = Long.valueOf(txtId.getText());
            details = billDetailDao.findByBillId(billId);
        }
        details.forEach(d -> {
            var amount = d.getUnitPrice() * d.getQuantity() * (1 - d.getDiscount() / 100.0);
            Object[] rowData = {
                    d.getDrinkName(),
                    String.format("%,.0f VNĐ", d.getUnitPrice()),
                    String.format("%.0f%%", d.getDiscount()),
                    d.getQuantity(), String.format("%,.0f VNĐ", amount)
            };
            model.addRow(rowData);
        });
    }

    @Override
    public void selectTimeRange() {
        TimeRange range = TimeRange.today();
        switch (cboTimeRanges.getSelectedIndex()) {
            case 0 -> range = TimeRange.today();
            case 1 -> range = TimeRange.thisWeek();
            case 2 -> range = TimeRange.thisMonth();
            case 3 -> range = TimeRange.thisQuarter();
            case 4 -> range = TimeRange.thisYear();
        }
        txtBegin.setText(XDate.format(range.getBegin(), "dd/MM/yyyy"));
        txtEnd.setText(XDate.format(range.getEnd(), "dd/MM/yyyy"));
        this.fillToTable();
    }

    @Override
    public void fillToTable() {
        // Lấy model của bảng và xóa các dòng cũ
        DefaultTableModel model = (DefaultTableModel) tblBills.getModel();
        model.setRowCount(0);

        // 1. Dùng định dạng "dd/MM/yyyy" thân thiện hơn với người dùng Việt Nam
        Date begin = XDate.parse(txtBegin.getText(), "dd/MM/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "dd/MM/yyyy");

        // Lấy danh sách hóa đơn từ CSDL
        items = dao.findByTimeRange(begin, end);

        // Duyệt qua danh sách và thêm từng hóa đơn vào bảng
        items.forEach(item -> {
            Object[] rowData = {
                    item.getId(),
                    // 2. Hiển thị "Card #" để rõ ràng hơn
                    "Card #" + item.getCardId(),
                    // 3. Định dạng ngày giờ checkin/checkout để dễ đọc
                    item.getCheckin() != null ? sdf.format(item.getCheckin()) : "",
                    item.getCheckout() != null ? sdf.format(item.getCheckout()) : "",
                    // 4. Dùng helper method để lấy tên trạng thái, giúp code sạch hơn
                    getStatusName(item.getStatus()),
                    item.getUsername(),
                    // 5. Giữ lại cột checkbox để thực hiện chức năng xóa nhiều mục
                    false
            };
            model.addRow(rowData);
        });
        // 6. Không gọi this.clear() ở đây để tránh xóa form không mong muốn
    }

    /*
     * @Override
     * public void setForm(Bill entity) {
     * txtId.setText(String.valueOf(entity.getId()));
     * txtUsername.setText(entity.getUsername());
     * txtCardId.setText(String.valueOf(entity.getCardId()));
     * 
     * // Checkin & Checkout
     * //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",
     * Locale.ENGLISH);
     * if (entity.getCheckin() != null) {
     * txtCheckin.setText(sdf.format(entity.getCheckin()));
     * } else {
     * txtCheckin.setText(""); // Xử lý trường hợp null
     * }
     * if (entity.getCheckout() != null) {
     * txtCheckout.setText(sdf.format(entity.getCheckout()));
     * } else {
     * txtCheckout.setText(""); // Xử lý trường hợp null
     * }
     * 
     * // Trạng thái
     * switch (entity.getStatus()) {
     * case 0 -> rdoServicing.setSelected(true);
     * case 1 -> rdoCompleted.setSelected(true);
     * case 2 -> rdoCanceled.setSelected(true);
     * default -> rdoServicing.setSelected(true); // Mặc định
     * }
     * this.fillBillDetails();
     * }
     */

    // Trong file BillManagerJDialog.java
    @Override
    public void setForm(Bill entity) {
        if (entity == null) {
            this.clear();
            return;
        }

        // PHẦN SỬA LỖI QUAN TRỌNG:
        // Nếu ID hoặc CardId là null, hiển thị chuỗi rỗng "" thay vì chữ "null"
        txtId.setText(entity.getId() != null ? String.valueOf(entity.getId()) : "");
        txtCardId.setText(entity.getCardId() != null ? String.valueOf(entity.getCardId()) : "");
        txtUsername.setText(entity.getUsername() != null ? entity.getUsername() : "");

        // Checkin & Checkout
        txtCheckin.setText(entity.getCheckin() != null ? sdf.format(entity.getCheckin()) : "");
        txtCheckout.setText(entity.getCheckout() != null ? sdf.format(entity.getCheckout()) : "");

        // Trạng thái
        if (entity.getStatus() != null) {
            switch (entity.getStatus()) {
                case 0 -> rdoServicing.setSelected(true);
                case 1 -> rdoCompleted.setSelected(true);
                case 2 -> rdoCanceled.setSelected(true);
                default -> buttonGroup1.clearSelection();
            }
        } else {
            buttonGroup1.clearSelection();
        }

        this.fillBillDetails();
    }

    @Override
    public Bill getForm() {
        Bill entity = new Bill();
        try {
            entity.setId(Long.valueOf(txtId.getText()));
            entity.setUsername(txtUsername.getText());
            entity.setCardId(Integer.valueOf(txtCardId.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn và mã thẻ phải là số!");
            return null;
        }

        // Checkin & Checkout
        String dateStrCheckin = txtCheckin.getText().trim();
        String dateStrCheckout = txtCheckout.getText().trim();
        // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",
        // Locale.ENGLISH);
        Date checkinDate = null;
        Date checkoutDate = null;
        try {
            if (!dateStrCheckin.isEmpty()) {
                checkinDate = sdf.parse(dateStrCheckin);
            }
            if (!dateStrCheckout.isEmpty()) {
                checkoutDate = sdf.parse(dateStrCheckout);
            } else if (rdoCompleted.isSelected() && dateStrCheckout.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thời điểm thanh toán phải được nhập khi hoàn tất!");
                return null;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Sai định dạng ngày giờ!\nĐúng: HH:mm:ss dd/MM/yyyy");
            return null;
        }
        entity.setCheckin(checkinDate);
        entity.setCheckout(checkoutDate);

        // Trạng thái
        int status = 0;
        if (rdoServicing.isSelected())
            status = 0;
        else if (rdoCompleted.isSelected())
            status = 1;
        else if (rdoCanceled.isSelected())
            status = 2;
        entity.setStatus(status);

        return entity;
    }

    @Override
    public void open() {
        this.setLocationRelativeTo(null);
        this.selectTimeRange();
        this.fillBillDetails();
        this.fillToTable();
        this.clear();
    }

    /*
     * @Override
     * public void edit() {
     * Bill entity = items.get(tblBills.getSelectedRow());
     * this.setForm(entity);
     * this.setEditable(true);
     * tabs.setSelectedIndex(1);
     * }
     */

    // Sửa lại phương thức edit()
    @Override
    public void edit() {
        int selectedRow = tblBills.getSelectedRow();
        if (selectedRow == -1)
            return; // Không có dòng nào được chọn

        // Lấy ID của hóa đơn trực tiếp từ cột 0 của bảng
        Long billId = (Long) tblBills.getValueAt(selectedRow, 0);

        // Tìm đúng hóa đơn trong danh sách 'items' bằng ID
        Bill entityToEdit = items.stream()
                .filter(bill -> bill.getId().equals(billId))
                .findFirst()
                .orElse(null); // Trả về null nếu không tìm thấy

        if (entityToEdit != null) {
            this.setForm(entityToEdit);
            this.setEditable(true);
            tabs.setSelectedIndex(1); // Chuyển sang tab biểu mẫu
        } else {
            XDialog.alert("Không tìm thấy hóa đơn để sửa!");
        }
    }

    @Override
    public void create() {
        String cardIdText = txtCardId.getText().trim();
        if (!cardIdText.isEmpty()) {
            try {
                Integer cardId = Integer.valueOf(cardIdText);
                Bill newBill = dao.findServicingByCardId(cardId); // Tự động gán checkin = new Date()
                this.setForm(newBill); // Cập nhật giao diện
                this.fillToTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã thẻ phải là số!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã thẻ!");
        }
        this.clear();
    }

    @Override
    public void update() {
        Bill entity = this.getForm();
        dao.update(entity);
        this.fillToTable();
    }

    @Override
    public void delete() {
        if (XDialog.confirm("Bạn thực sự muốn xóa?")) {
            Long id = Long.valueOf(txtId.getText());
            dao.deleteById(id);
            this.fillToTable();
            this.clear();
        }
    }

    @Override
    public void clear() {
        this.setForm(new Bill());
        this.setEditable(false);
    }

    @Override
    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);
        txtUsername.setEnabled(true);
        txtCardId.setEnabled(true);
        txtCheckin.setEnabled(true);
        txtCheckout.setEnabled(true);
        rdoServicing.setEnabled(true);

        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);

        int rowCount = tblBills.getRowCount();
        btnMoveFirst.setEnabled(editable && rowCount > 0);
        btnMovePrevious.setEnabled(editable && rowCount > 0);
        btnMoveNext.setEnabled(editable && rowCount > 0);
        btnMoveLast.setEnabled(editable && rowCount > 0);
    }

    @Override
    public void checkAll() {
        this.setCheckedAll(true);
    }

    @Override
    public void uncheckAll() {
        this.setCheckedAll(false);
    }

    private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblBills.getRowCount(); i++) {
            tblBills.setValueAt(checked, i, 6);
        }
    }

    @Override
    public void deleteCheckedItems() {
        if (XDialog.confirm("Bạn thực sự muốn xóa các mục chọn?")) {
            boolean deleted = false;
            for (int i = 0; i < tblBills.getRowCount(); i++) {
                Object value = tblBills.getValueAt(i, 6);
                if (value instanceof Boolean && (Boolean) value) {
                    Long id = (Long) tblBills.getValueAt(i, 0); // Lấy Id từ cột 0
                    if (id != null) {
                        dao.deleteById(id);
                        deleted = true;
                        System.out.println("Deleted bill: " + id);
                    }
                }
            }
            if (deleted) {
                this.fillToTable(); // Cập nhật lại items
            } else {
                XDialog.alert("Không có mục nào được chọn để xóa hoặc lỗi xóa!");
            }
        }
    }

    @Override
    public void moveFirst() {
        this.moveTo(0);
    }

    @Override
    public void movePrevious() {
        this.moveTo(tblBills.getSelectedRow() - 1);
    }

    @Override
    public void moveNext() {
        this.moveTo(tblBills.getSelectedRow() + 1);
    }

    @Override
    public void moveLast() {
        this.moveTo(tblBills.getRowCount() - 1);
    }

    @Override
    public void moveTo(int index) {
        if (index < 0) {
            this.moveLast();
        } else if (index >= tblBills.getRowCount()) {
            this.moveFirst();
        } else {
            tblBills.clearSelection();
            tblBills.setRowSelectionInterval(index, index);
            this.edit();
        }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBills = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        cboTimeRanges = new javax.swing.JComboBox<>();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtCardId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCheckin = new javax.swing.JTextField();
        txtCheckout = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rdoServicing = new javax.swing.JRadioButton();
        rdoCompleted = new javax.swing.JRadioButton();
        rdoCanceled = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBillDetails = new javax.swing.JTable();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý phiếu bán hàng");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(245, 236, 213));

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        tblBills.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null }
                },
                new String[] {
                        "Mã phiếu", "Thẻ số", "Thời điểm tạo", "Thời điểm thanh toán", "Trạng thái", "Người tạo", ""
                }) {
            Class[] types = new Class[] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        tblBills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBillsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBills);

        jLabel1.setText("Từ ngày:");

        jLabel2.setText("Đến ngày:");

        btnFilter.setText("Lọc");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        cboTimeRanges.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay" }));
        cboTimeRanges.setSelectedIndex(4);
        cboTimeRanges.setToolTipText("");

        btnCheckAll.setBackground(new java.awt.Color(63, 195, 107));
        btnCheckAll.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckAll.setText("Chọn tất cả");
        btnCheckAll.setIcon(XIcon.getIcon("/poly/cafe/images/icons/list.png", 16, 16));
        btnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAllActionPerformed(evt);
            }
        });

        btnUncheckAll.setBackground(new java.awt.Color(247, 181, 58));
        btnUncheckAll.setForeground(new java.awt.Color(255, 255, 255));
        btnUncheckAll.setText("Bỏ chọn tất cả");
        btnUncheckAll.setIcon(XIcon.getIcon("/poly/cafe/images/icons/refresh.png", 16, 16));
        btnUncheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUncheckAllActionPerformed(evt);
            }
        });

        btnDeleteCheckedItems.setBackground(new java.awt.Color(218, 68, 68));
        btnDeleteCheckedItems.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteCheckedItems.setText("Xóa các mục chọn");
        btnDeleteCheckedItems.setIcon(XIcon.getIcon("/poly/cafe/images/icons/delete.png", 16, 16));
        btnDeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedItemsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                .createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(btnDeleteCheckedItems,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                150,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(btnUncheckAll,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                150,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(btnCheckAll,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                150,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(jLabel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                70,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(txtBegin,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                140,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jLabel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                70,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(txtEnd,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                140,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(btnFilter,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                60,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(cboTimeRanges,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                100,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(20, 20, 20)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnFilter)
                                        .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCheckAll)
                                        .addComponent(btnUncheckAll)
                                        .addComponent(btnDeleteCheckedItems))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        tabs.addTab("DANH SÁCH", jPanel1);

        jPanel2.setBackground(new java.awt.Color(245, 236, 213));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Mã phiếu:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Thẻ số:");

        txtCardId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCardIdActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Thời điểm tạo:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Thời điểm thanh toán:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Trạng thái:");

        buttonGroup1.add(rdoServicing);
        rdoServicing.setText("Servicing");

        buttonGroup1.add(rdoCompleted);
        rdoCompleted.setText("Completed");

        buttonGroup1.add(rdoCanceled);
        rdoCanceled.setText("Canceled");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Người tạo:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Phiếu chi tiết:");

        tblBillDetails.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "Đồ uống", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"
                }));
        jScrollPane2.setViewportView(tblBillDetails);

        btnCreate.setBackground(new java.awt.Color(122, 92, 62));
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Tạo mới");
        btnCreate.setIcon(XIcon.getIcon("/poly/cafe/images/icons/add.png", 16, 16));
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(122, 92, 62));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Cập nhật");
        btnUpdate.setIcon(XIcon.getIcon("/poly/cafe/images/icons/edit.png", 16, 16));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(122, 92, 62));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Xóa");
        btnDelete.setIcon(XIcon.getIcon("/poly/cafe/images/icons/delete.png", 16, 16));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(122, 92, 62));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Nhập mới");
        btnClear.setIcon(XIcon.getIcon("/poly/cafe/images/icons/refresh.png", 16, 16));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnMoveFirst.setBackground(new java.awt.Color(122, 92, 62));
        btnMoveFirst.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        btnMovePrevious.setBackground(new java.awt.Color(122, 92, 62));
        btnMovePrevious.setForeground(new java.awt.Color(255, 255, 255));
        btnMovePrevious.setText("<<");
        btnMovePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovePreviousActionPerformed(evt);
            }
        });

        btnMoveNext.setBackground(new java.awt.Color(122, 92, 62));
        btnMoveNext.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveNext.setText(">>");
        btnMoveNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveNextActionPerformed(evt);
            }
        });

        btnMoveLast.setBackground(new java.awt.Color(122, 92, 62));
        btnMoveLast.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveLast.setText(">|");
        btnMoveLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveLastActionPerformed(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout
                                                .createSequentialGroup()
                                                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jSeparator1)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel9)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(txtCheckin,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                275,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                290,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel6,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                290,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txtCheckout,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                275,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        false)
                                                                        .addComponent(jLabel7,
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                jPanel2Layout.createSequentialGroup()
                                                                                        .addComponent(rdoServicing)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(rdoCompleted)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(rdoCanceled))))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(txtId,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                275,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                290,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(txtCardId,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                275,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                290,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel8,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                260,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txtUsername,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                275,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(20, 20, 20)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 16,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel3)
                                                .addComponent(jLabel8)))
                                .addGap(6, 6, 6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCardId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel6))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtCheckin,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtCheckout,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(rdoServicing)
                                                        .addComponent(rdoCompleted)
                                                        .addComponent(rdoCanceled))))
                                .addGap(15, 15, 15)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 152,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCreate)
                                        .addComponent(btnDelete)
                                        .addComponent(btnMoveFirst)
                                        .addComponent(btnMovePrevious)
                                        .addComponent(btnMoveNext)
                                        .addComponent(btnMoveLast)
                                        .addComponent(btnUpdate)
                                        .addComponent(btnClear))
                                .addContainerGap(29, Short.MAX_VALUE)));

        tabs.addTab("BIỂU MẪU", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabs));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCardIdActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtCardIdActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txtCardIdActionPerformed

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }// GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUncheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUncheckAllActionPerformed
        // TODO add your handling code here:
        this.uncheckAll();
    }// GEN-LAST:event_btnUncheckAllActionPerformed

    private void btnDeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteCheckedItemsActionPerformed
        // TODO add your handling code here:
        this.deleteCheckedItems();
    }// GEN-LAST:event_btnDeleteCheckedItemsActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        // this.create();
        // Lấy dữ liệu từ form người dùng đã nhập
        Bill newBill = this.getForm();
        if (newBill != null) {
            try {
                dao.create(newBill); // Lưu hóa đơn mới vào CSDL
                XDialog.alert("Tạo mới hóa đơn thành công!");
                this.fillToTable(); // Cập nhật lại bảng
                this.clear(); // Xóa trắng form
                tabs.setSelectedIndex(0); // Quay về tab danh sách
            } catch (Exception e) {
                XDialog.alert("Lỗi khi tạo mới hóa đơn!");
                e.printStackTrace();
            }
        }
    }// GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }// GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        this.delete();
    }// GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        // this.clear();
        this.clear();
        tabs.setSelectedIndex(1); // Chuyển sang tab biểu mẫu để người dùng nhập
    }// GEN-LAST:event_btnClearActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
        this.moveFirst();
    }// GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMovePreviousActionPerformed
        // TODO add your handling code here:
        this.movePrevious();
    }// GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveNextActionPerformed
        // TODO add your handling code here:
        this.moveNext();
    }// GEN-LAST:event_btnMoveNextActionPerformed

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveLastActionPerformed
        // TODO add your handling code here:
        this.moveLast();
    }// GEN-LAST:event_btnMoveLastActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }// GEN-LAST:event_formWindowOpened

    private void tblBillsMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tblBillsMouseClicked
        // TODO add your handling code here:
        this.edit();
    }// GEN-LAST:event_tblBillsMouseClicked

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
        this.selectTimeRange();
    }// GEN-LAST:event_btnFilterActionPerformed

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
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }
        // </editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillManagerJDialog dialog = new BillManagerJDialog(new javax.swing.JFrame(), true);
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

    /**
     * Initialize icons for buttons
     */
    private void initIcons() {
        btnCheckAll.setIcon(XIcon.getIcon("/poly/cafe/images/icons/list.png", 16, 16));
        btnUncheckAll.setIcon(XIcon.getIcon("/poly/cafe/images/icons/refresh.png", 16, 16));
        btnDeleteCheckedItems.setIcon(XIcon.getIcon("/poly/cafe/images/icons/delete.png", 16, 16));
        btnCreate.setIcon(XIcon.getIcon("/poly/cafe/images/icons/add.png", 16, 16));
        btnUpdate.setIcon(XIcon.getIcon("/poly/cafe/images/icons/edit.png", 16, 16));
        btnDelete.setIcon(XIcon.getIcon("/poly/cafe/images/icons/delete.png", 16, 16));
        btnClear.setIcon(XIcon.getIcon("/poly/cafe/images/icons/refresh.png", 16, 16));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCheckedItems;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUncheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rdoCanceled;
    private javax.swing.JRadioButton rdoCompleted;
    private javax.swing.JRadioButton rdoServicing;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblBillDetails;
    private javax.swing.JTable tblBills;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtCardId;
    private javax.swing.JTextField txtCheckin;
    private javax.swing.JTextField txtCheckout;
    private javax.swing.JTextField txtEnd;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
