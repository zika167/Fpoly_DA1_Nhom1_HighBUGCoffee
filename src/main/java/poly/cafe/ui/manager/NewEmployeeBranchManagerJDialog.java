/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.UserDAO;
import poly.cafe.dao.impl.UserDAOImpl;
import poly.cafe.entity.User;
import poly.cafe.util.XDialog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import poly.cafe.dao.ShopDAO;
import poly.cafe.dao.impl.ShopDAOImpl;
import poly.cafe.entity.Shop;
import poly.cafe.util.XIcon;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import poly.cafe.util.IconUtils;

/**
 *
 * @author dthie
 */
public class NewEmployeeBranchManagerJDialog extends javax.swing.JDialog implements EmployeeBranchManagerController {

    private final UserDAO userDao = new UserDAOImpl();
    private final ShopDAO shopDao = new ShopDAOImpl();
    private User currentUser; // Người dùng đang chỉnh sửa
    private List<User> users = new ArrayList<>();
    private List<Shop> shops = new ArrayList<>();
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates new form HomePage
     */
    public NewEmployeeBranchManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        initTableHeaders();
        loadBranches();
        loadEmployeeTable();
        initIcons();
    }

    private void loadEmployeeTable() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setRowCount(0);
        users = userDao.findAll();
        if (users == null)
            users = List.of();

        // Sắp xếp: chain_manager lên đầu → theo mã chi nhánh tăng dần → trong cùng chi
        // nhánh: branch_manager trước staff → theo username
        List<User> sorted = users.stream()
                .sorted((u1, u2) -> {
                    // 1) chain_manager lên đầu tiên
                    boolean u1Chain = u1.getRole() == User.Role.chain_manager;
                    boolean u2Chain = u2.getRole() == User.Role.chain_manager;
                    if (u1Chain && !u2Chain)
                        return -1;
                    if (!u1Chain && u2Chain)
                        return 1;

                    // 2) so sánh shopId tăng dần, nullsLast
                    String s1 = u1.getShopId();
                    String s2 = u2.getShopId();
                    int shopCmp = Comparator.nullsLast(String::compareTo).compare(s1, s2);
                    if (shopCmp != 0)
                        return shopCmp;

                    // 3) trong cùng chi nhánh: branch_manager trước staff
                    boolean u1BranchManager = u1.getRole() == User.Role.branch_manager;
                    boolean u2BranchManager = u2.getRole() == User.Role.branch_manager;
                    if (u1BranchManager && !u2BranchManager)
                        return -1;
                    if (!u1BranchManager && u2BranchManager)
                        return 1;

                    // 4) theo username
                    return Comparator.nullsLast(String::compareTo).compare(u1.getUsername(), u2.getUsername());
                })
                .collect(Collectors.toList());

        sorted.forEach(u -> {
            Object[] row = new Object[] {
                    u.getUsername(),
                    u.getPassword(),
                    u.getFullname(),
                    u.getPhoto(),
                    u.getRole(),
                    u.getShopId(),
                    false
            };
            model.addRow(row);
        });
    }

    private void initTableHeaders() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setColumnIdentifiers(
                new String[] { "Tên đăng nhập", "Mật khẩu", "Họ và tên", "Hình ảnh", "Vai trò", "Mã chi nhánh", "" });
    }

    private void loadBranches() {
        shops = shopDao.findAll();
        if (shops == null)
            shops = new ArrayList<>();
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        // Hiển thị theo Id (mã chi nhánh)
        for (Shop s : shops) {
            cbModel.addElement(s.getId());
        }
        cboBranch.setModel(cbModel);
    }

    @Override
    public void clear() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullname.setText("");
        txtConfirmPassword.setText("");
        buttonGroup1.clearSelection(); // role group
        lblImg.setIcon(null);
        lblImg.setToolTipText("");
        if (cboBranch.getItemCount() > 0)
            cboBranch.setSelectedIndex(0);
        currentUser = null;
    }

    @Override
    public void create() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String fullname = txtFullname.getText().trim();
        String branchId = (String) cboBranch.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || branchId == null || branchId.isEmpty()) {
            XDialog.alert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            XDialog.alert("Xác nhận mật khẩu không khớp!");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullname(fullname);
        user.setEnabled(true);
        String photo = lblImg.getToolTipText();
        if (photo == null || photo.isBlank()) {
            XDialog.alert("Vui lòng chọn ảnh đại diện!");
            return;
        }
        // Cắt tên file nếu quá dài (phòng lỗi DB giống UserManager)
        photo = photo.trim();
        if (photo.length() > 50) {
            int lastDotIndex = photo.lastIndexOf('.');
            if (lastDotIndex > 0) {
                String name = photo.substring(0, lastDotIndex);
                String extension = photo.substring(lastDotIndex);
                if (name.length() > 45) {
                    name = name.substring(0, 45);
                }
                photo = name + extension;
            } else {
                photo = photo.substring(0, 50);
            }
        }
        user.setPhoto(photo);
        if (rdoManager.isSelected()) {
            user.setRole(User.Role.branch_manager);
        } else {
            user.setRole(User.Role.staff);
        }
        user.setShopId(branchId);

        if (userDao.create(user) != null) {
            XDialog.alert("Thêm nhân viên thành công!");
            loadEmployeeTable();
            clear();
        } else {
            XDialog.alert("Thêm nhân viên thất bại!");
        }
    }

    @Override
    public void update() {
        if (currentUser == null) {
            XDialog.alert("Vui lòng chọn nhân viên để sửa!");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullname = txtFullname.getText().trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String branchId = (String) cboBranch.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || branchId == null || branchId.isEmpty()) {
            XDialog.alert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            XDialog.alert("Xác nhận mật khẩu không khớp!");
            return;
        }

        currentUser.setUsername(username);
        currentUser.setPassword(password);
        currentUser.setFullname(fullname);
        currentUser.setEnabled(true);
        String photo2 = lblImg.getToolTipText();
        if (photo2 == null || photo2.isBlank()) {
            XDialog.alert("Vui lòng chọn ảnh đại diện!");
            return;
        }
        photo2 = photo2.trim();
        if (photo2.length() > 50) {
            int lastDotIndex = photo2.lastIndexOf('.');
            if (lastDotIndex > 0) {
                String name = photo2.substring(0, lastDotIndex);
                String extension = photo2.substring(lastDotIndex);
                if (name.length() > 45) {
                    name = name.substring(0, 45);
                }
                photo2 = name + extension;
            } else {
                photo2 = photo2.substring(0, 50);
            }
        }
        currentUser.setPhoto(photo2);
        if (rdoManager.isSelected()) {
            currentUser.setRole(User.Role.branch_manager);
        } else {
            currentUser.setRole(User.Role.staff);
        }
        currentUser.setShopId(branchId);

        userDao.update(currentUser);
        XDialog.alert("Cập nhật nhân viên thành công!");
        loadEmployeeTable();
        clear();
    }

    @Override
    public void delete() {
        if (currentUser == null) {
            XDialog.alert("Vui lòng chọn nhân viên để xóa!");
            return;
        }

        if (XDialog.confirm("Bạn có chắc muốn xóa nhân viên này?")) {
            try {
                userDao.deleteById(currentUser.getUsername());
                XDialog.alert("Xóa nhân viên thành công!");
                loadEmployeeTable();
                clear();
            } catch (Exception ex) {
                XDialog.alert("Xóa nhân viên thất bại!");
            }
        }
    }

    private void selectEmployee() {
        int row = tblUsers.getSelectedRow();
        if (row >= 0) {
            String username = (String) tblUsers.getValueAt(row, 0);
            if (username == null)
                return;
            currentUser = userDao.findById(username);
            if (currentUser == null)
                return;
            txtUsername.setText(currentUser.getUsername());
            txtPassword.setText(currentUser.getPassword());
            txtConfirmPassword.setText(currentUser.getPassword());
            txtFullname.setText(currentUser.getFullname());
            // Ảnh
            String imageName = currentUser.getPhoto();
            setImageFromName(imageName);
            // Vai trò
            if (currentUser.getRole() == User.Role.branch_manager) {
                rdoManager.setSelected(true);
            } else {
                rdoStaff.setSelected(true);
            }
            // Chi nhánh
            if (currentUser.getShopId() != null) {
                cboBranch.setSelectedItem(currentUser.getShopId());
            }
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFullname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboBranch = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblImg = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        rdoManager = new javax.swing.JRadioButton();
        rdoStaff = new javax.swing.JRadioButton();
        txtPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tạo mới nhân viên");

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        jPanel2.setBackground(new java.awt.Color(245, 236, 213));

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tên đăng nhập", "Mật khẩu", "Họ và tên", "Hình ảnh", "Vai trò", "Mã chi nhánh", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsers);

        btnCheckAll.setBackground(new java.awt.Color(63, 195, 107));
        btnCheckAll.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckAll.setText("Chọn tất cả");
        btnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAllActionPerformed(evt);
            }
        });

        btnUncheckAll.setBackground(new java.awt.Color(247, 181, 58));
        btnUncheckAll.setForeground(new java.awt.Color(255, 255, 255));
        btnUncheckAll.setText("Bỏ chọn tất cả");
        btnUncheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUncheckAllActionPerformed(evt);
            }
        });

        btnDeleteCheckedItems.setBackground(new java.awt.Color(218, 68, 68));
        btnDeleteCheckedItems.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteCheckedItems.setText("Xóa các mục chọn");
        btnDeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedItemsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnDeleteCheckedItems, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUncheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckAll)
                    .addComponent(btnUncheckAll)
                    .addComponent(btnDeleteCheckedItems))
                .addGap(15, 15, 15))
        );

        tabs.addTab("DANH SÁCH", jPanel2);

        jPanel3.setBackground(new java.awt.Color(245, 236, 213));

        jLabel1.setText("Họ và tên");

        jLabel2.setText("Mã chi nhánh");

        jSeparator1.setForeground(new java.awt.Color(102, 102, 102));

        btnCreate.setBackground(new java.awt.Color(122, 92, 62));
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Tạo mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(122, 92, 62));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Cập nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(122, 92, 62));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(122, 92, 62));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Nhập mới");
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

        jLabel3.setText("Tên đăng nhập");

        jLabel4.setText("Xác nhận mật khẩu");

        jLabel5.setText("Mật khẩu");

        lblImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImgMouseClicked(evt);
            }
        });

        jLabel7.setText("Vai trò");

        buttonGroup1.add(rdoManager);
        rdoManager.setText("Quản lý");

        buttonGroup1.add(rdoStaff);
        rdoStaff.setText("Nhân viên");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rdoManager)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoStaff))
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFullname)
                            .addComponent(jLabel2)
                            .addComponent(cboBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoManager)
                            .addComponent(rdoStaff)
                            .addComponent(cboBranch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblImg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnDelete)
                    .addComponent(btnMoveFirst)
                    .addComponent(btnMovePrevious)
                    .addComponent(btnMoveNext)
                    .addComponent(btnMoveLast)
                    .addComponent(btnUpdate)
                    .addComponent(btnClear))
                .addGap(15, 15, 15))
        );

        tabs.addTab("BIỂU MẪU", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblUsersMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tblUsersMouseClicked
        if (evt.getClickCount() == 2) {
            selectEmployee();
            tabs.setSelectedIndex(1);
        }
    }// GEN-LAST:event_tblUsersMouseClicked

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCheckAllActionPerformed
        setCheckedAll(true);
    }// GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUncheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUncheckAllActionPerformed
        setCheckedAll(false);
    }// GEN-LAST:event_btnUncheckAllActionPerformed

    private void btnDeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteCheckedItemsActionPerformed
        deleteCheckedItems();
    }// GEN-LAST:event_btnDeleteCheckedItemsActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateActionPerformed
        this.create();
    }// GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUpdateActionPerformed
        this.update();
    }// GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteActionPerformed
        this.delete();
    }// GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnClearActionPerformed
        this.clear();
    }// GEN-LAST:event_btnClearActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveFirstActionPerformed
        moveFirst();
    }// GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMovePreviousActionPerformed
        movePrevious();
    }// GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveNextActionPerformed
        moveNext();
    }// GEN-LAST:event_btnMoveNextActionPerformed

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnMoveLastActionPerformed
        moveLast();
    }// GEN-LAST:event_btnMoveLastActionPerformed

    private void lblImgMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lblImgMouseClicked
        if (evt.getClickCount() == 1) {
            chooseFile();
        }
    }// GEN-LAST:event_lblImgMouseClicked

    // </editor-fold>
    // </editor-fold>

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
            java.util.logging.Logger.getLogger(NewEmployeeBranchManagerJDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewEmployeeBranchManagerJDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewEmployeeBranchManagerJDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewEmployeeBranchManagerJDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewEmployeeBranchManagerJDialog dialog = new NewEmployeeBranchManagerJDialog(new javax.swing.JFrame(),
                        true);
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
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCheckedItems;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUncheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboBranch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblImg;
    private javax.swing.JRadioButton rdoManager;
    private javax.swing.JRadioButton rdoStaff;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblUsers;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtFullname;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

    private void setImageFromName(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            lblImg.setIcon(null);
            lblImg.setToolTipText("");
            return;
        }
        lblImg.setToolTipText(imageName);
        boolean imageFound = false;
        String avatarsImagePath = "/poly/cafe/images/avatars/" + imageName;
        try {
            XIcon.setIcon(lblImg, avatarsImagePath);
            imageFound = true;
        } catch (Exception ignore) {
        }
        if (!imageFound) {
            String rootImagePath = "/poly/cafe/images/" + imageName;
            try {
                XIcon.setIcon(lblImg, rootImagePath);
                imageFound = true;
            } catch (Exception ignore) {
            }
        }
        if (!imageFound) {
            File avatarsFile = new File("src/main/java/poly/cafe/images/avatars/" + imageName);
            if (avatarsFile.exists()) {
                XIcon.setIcon(lblImg, avatarsFile);
                imageFound = true;
            } else {
                File rootFile = new File("src/main/java/poly/cafe/images/" + imageName);
                if (rootFile.exists()) {
                    XIcon.setIcon(lblImg, rootFile);
                    imageFound = true;
                }
            }
        }
        if (!imageFound) {
            lblImg.setIcon(null);
        }
    }

    // ===== Helpers giống UserManager =====
    private void initIcons() {
        IconUtils.setButtonIconSafe(btnCheckAll, "/poly/cafe/images/icons/list.png", 16, 16);
        IconUtils.setButtonIconSafe(btnUncheckAll, "/poly/cafe/images/icons/refresh.png", 16, 16);
        IconUtils.setButtonIconSafe(btnDeleteCheckedItems, "/poly/cafe/images/icons/delete.png", 16, 16);
        IconUtils.setButtonIconSafe(btnCreate, "/poly/cafe/images/icons/add.png", 16, 16);
        IconUtils.setButtonIconSafe(btnUpdate, "/poly/cafe/images/icons/edit.png", 16, 16);
        IconUtils.setButtonIconSafe(btnDelete, "/poly/cafe/images/icons/delete.png", 16, 16);
        IconUtils.setButtonIconSafe(btnClear, "/poly/cafe/images/icons/refresh.png", 16, 16);
    }

    // Chọn ảnh: ưu tiên folder avatars, giữ nguyên tên file
    private void chooseFile() {
        File currentDir = new File("src/main/java/poly/cafe/images/avatars");
        if (currentDir.exists()) {
            fileChooser.setCurrentDirectory(currentDir);
        }
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File avatarsDir = new File("src/main/java/poly/cafe/images/avatars");
            if (selectedFile.getParentFile().equals(avatarsDir)) {
                lblImg.setToolTipText(selectedFile.getName());
                XIcon.setIcon(lblImg, selectedFile);
            } else {
                try {
                    java.nio.file.Path target = avatarsDir.toPath().resolve(selectedFile.getName());
                    java.nio.file.Files.createDirectories(avatarsDir.toPath());
                    java.nio.file.Files.copy(selectedFile.toPath(), target,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    lblImg.setToolTipText(selectedFile.getName());
                    XIcon.setIcon(lblImg, target.toFile());
                } catch (Exception ex) {
                    lblImg.setToolTipText(selectedFile.getName());
                    XIcon.setIcon(lblImg, selectedFile);
                }
            }
        }
    }

    private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblUsers.getRowCount(); i++) {
            tblUsers.setValueAt(checked, i, 6);
        }
    }

    private void deleteCheckedItems() {
        if (!XDialog.confirm("Bạn thực sự muốn xóa các mục chọn?")) {
            return;
        }
        java.util.List<String> toDelete = new java.util.ArrayList<>();
        for (int i = 0; i < tblUsers.getRowCount(); i++) {
            Object value = tblUsers.getValueAt(i, 6);
            if (value instanceof Boolean && (Boolean) value) {
                String username = (String) tblUsers.getValueAt(i, 0);
                if (username != null && !username.isBlank()) {
                    toDelete.add(username);
                }
            }
        }
        if (toDelete.isEmpty()) {
            XDialog.alert("Không có mục nào được chọn để xóa!");
            return;
        }
        for (String username : toDelete) {
            try {
                userDao.deleteById(username);
            } catch (Exception ex) {
                // bỏ qua từng lỗi riêng lẻ, tiếp tục xóa mục khác
            }
        }
        loadEmployeeTable();
        clear();
    }

    private void moveFirst() {
        moveTo(0);
    }

    private void movePrevious() {
        int idx = tblUsers.getSelectedRow();
        if (idx < 0)
            idx = 0;
        moveTo(idx - 1);
    }

    private void moveNext() {
        int idx = tblUsers.getSelectedRow();
        moveTo(idx + 1);
    }

    private void moveLast() {
        moveTo(tblUsers.getRowCount() - 1);
    }

    private void moveTo(int index) {
        int rowCount = tblUsers.getRowCount();
        if (rowCount == 0)
            return;
        if (index < 0)
            index = 0;
        if (index >= rowCount)
            index = rowCount - 1;
        tblUsers.clearSelection();
        tblUsers.setRowSelectionInterval(index, index);
        selectEmployee();
        tabs.setSelectedIndex(1);
    }
}

interface EmployeeBranchManagerController {
    void create();

    void update();

    void delete();

    void clear();
}
