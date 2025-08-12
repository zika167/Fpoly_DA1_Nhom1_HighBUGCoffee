
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.awt.Image;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.UserDAO;
import poly.cafe.dao.impl.UserDAOImpl;
import poly.cafe.entity.Drink;
import poly.cafe.entity.User;
import static poly.cafe.entity.User.Role.chain_manager;
import static poly.cafe.entity.User.Role.branch_manager;
import static poly.cafe.entity.User.Role.staff;
import poly.cafe.util.XDialog;
import poly.cafe.util.XIcon;
import poly.cafe.util.IconUtils;
import javax.swing.JButton;

/**
 *
 * @author wangquockhanh
 */
public class UserManagerJDialog extends javax.swing.JDialog implements UserController {

    /**
     * Creates new form UserManagerJDialog
     */
    public UserManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initIcons();
    }

    UserDAO dao = new UserDAOImpl();
    List<User> items = List.of();
    JFileChooser fileChooser = new JFileChooser();

    // Thông tin user hiện tại đang đăng nhập
    private User currentUser;
    private String currentUserBranch;

    /**
     * Set thông tin user hiện tại đang đăng nhập
     * 
     * @param user     User hiện tại
     * @param branchId ID chi nhánh của user
     */
    public void setCurrentUser(User user, String branchId) {
        this.currentUser = user;
        this.currentUserBranch = branchId;
    }

    /**
     * Chọn file hình ảnh cho avatar
     */
    public void chooseFile() {
        // Kiểm tra xem file có phải từ thư mục avatars không
        File currentDir = new File("src/main/java/poly/cafe/images/avatars");
        if (currentDir.exists()) {
            fileChooser.setCurrentDirectory(currentDir);
        }

        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Kiểm tra xem file đã có trong thư mục avatars chưa
            File avatarsDir = new File("src/main/java/poly/cafe/images/avatars");
            if (selectedFile.getParentFile().equals(avatarsDir)) {
                // File đã có trong thư mục avatars, chỉ cần lưu tên file
                lblImg.setToolTipText(selectedFile.getName());
                XIcon.setIcon(lblImg, selectedFile);
                System.out.println("Avatar selected from avatars folder: " + selectedFile.getName());
            } else {
                // File từ nơi khác, copy vào thư mục avatars nhưng lưu tên file gốc
                File file = XIcon.copyTo(selectedFile, "images/avatars");
                // Lưu tên file gốc thay vì tên file được tạo bởi copyTo
                lblImg.setToolTipText(selectedFile.getName());
                XIcon.setIcon(lblImg, file);
                System.out.println("Avatar copied to avatars folder: " + file.getName() + " but saved original name: "
                        + selectedFile.getName());
            }
        }
    }

    @Override
    public void open() {
        this.setLocationRelativeTo(null);

        // Kiểm tra quyền truy cập
        if (currentUser == null) {
            XDialog.alert("Không có quyền truy cập User Manager!");
            this.dispose();
            return;
        }

        // Chỉ cho phép chain_manager và branch_manager truy cập
        User.Role currentRole = currentUser.getRole();
        if (currentRole != User.Role.chain_manager && currentRole != User.Role.branch_manager) {
            XDialog.alert("Chỉ quản lý mới có quyền truy cập User Manager!");
            this.dispose();
            return;
        }

        this.fillToTable();
        this.clear();
    }

    @Override
    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setRowCount(0);

        // Kiểm tra quyền truy cập
        if (currentUser == null) {
            XDialog.alert("Không có thông tin người dùng hiện tại!");
            return;
        }

        User.Role currentRole = currentUser.getRole();
        if (currentRole == User.Role.chain_manager) {
            items = dao.findAll();
        } else if (currentRole == User.Role.branch_manager) {
            String branchId = currentUser.getShopId();
            items = dao.findAll().stream()
                    .filter(u -> branchId != null && branchId.equals(u.getShopId()))
                    .collect(Collectors.toList());
        } else {
            items = java.util.List.of(); // staff không được xem danh sách
        }

        // Sắp xếp danh sách để branch-manager lên đầu tiên
        List<User> sortedItems = items.stream()
                .sorted((u1, u2) -> {
                    // branch-manager lên đầu
                    if (u1.getRole() == User.Role.branch_manager && u2.getRole() != User.Role.branch_manager) {
                        return -1;
                    }
                    if (u1.getRole() != User.Role.branch_manager && u2.getRole() == User.Role.branch_manager) {
                        return 1;
                    }
                    // Sau đó sắp xếp theo username
                    return u1.getUsername().compareTo(u2.getUsername());
                })
                .collect(Collectors.toList());

        // Đồng bộ lại danh sách items với thứ tự đã hiển thị để
        // các thao tác chọn dòng (edit, điều hướng) lấy đúng phần tử
        items = sortedItems;

        items.forEach(item -> {
            Object[] rowData = {
                    item.getUsername(),
                    item.getPassword(),
                    item.getFullname(),
                    item.getPhoto(),
                    item.getRole(),
                    item.isEnabled(),
                    false
            };
            model.addRow(rowData);
        });
    }

    @Override
    public void edit() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= items.size()) {
            return;
        }
        User entity = items.get(selectedRow);
        this.setForm(entity);
        this.setEditable(true);
        tabs.setSelectedIndex(1);
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
        for (int i = 0; i < tblUsers.getRowCount(); i++) {
            tblUsers.setValueAt(checked, i, 6);
        }
    }

    @Override
    public void deleteCheckedItems() {
        if (XDialog.confirm("Bạn thực sự muốn xóa các mục chọn?")) {
            // Kiểm tra quyền xóa user
            if (currentUser == null) {
                XDialog.alert("Không có quyền xóa người dùng!");
                return;
            }

            boolean deleted = false;
            for (int i = 0; i < tblUsers.getRowCount(); i++) {
                Object value = tblUsers.getValueAt(i, 6); // Cột checkbox là cột 6
                if (value instanceof Boolean && (Boolean) value) {
                    String username = (String) tblUsers.getValueAt(i, 0); // Lấy username từ cột 0
                    if (username != null && !username.trim().isEmpty()) {
                        // Nếu là branch_manager, kiểm tra quyền xóa
                        if (currentUser.getRole() == User.Role.branch_manager) {
                            // TODO: Kiểm tra role của user bị xóa khi có bảng User
                            // Hiện tại tạm thời cho phép xóa
                        }

                        dao.deleteById(username);
                        deleted = true;
                        System.out.println("Deleted user: " + username); // Debug log
                    }
                }
            }
            if (deleted) {
                this.fillToTable();
            } else {
                XDialog.alert("Không có mục nào được chọn để xóa hoặc lỗi xóa!");
            }
        }
    }

    @Override
    public void setForm(User entity) {
        txtUsername.setText(entity.getUsername());
        txtPassword.setText(entity.getPassword());
        txtFullname.setText(entity.getFullname());

        // Xử lý hiển thị hình ảnh
        String imageName = entity.getPhoto();
        lblImg.setToolTipText(imageName);
        if (imageName != null && !imageName.isEmpty()) {
            boolean imageFound = false;

            // Thử tìm trong thư mục avatars trước
            String avatarsImagePath = "/poly/cafe/images/avatars/" + imageName;
            try {
                XIcon.setIcon(lblImg, avatarsImagePath);
                System.out.println("Avatar loaded from avatars folder: " + avatarsImagePath);
                imageFound = true;
            } catch (Exception e) {
                // Nếu không tìm thấy trong avatars, thử tìm trong thư mục gốc images
                String rootImagePath = "/poly/cafe/images/" + imageName;
                try {
                    XIcon.setIcon(lblImg, rootImagePath);
                    System.out.println("Avatar loaded from root images folder: " + rootImagePath);
                    imageFound = true;
                } catch (Exception e2) {
                    // Thử tìm bằng đường dẫn tuyệt đối
                    File avatarsFile = new File("src/main/java/poly/cafe/images/avatars/" + imageName);
                    if (avatarsFile.exists()) {
                        XIcon.setIcon(lblImg, avatarsFile);
                        System.out.println("Avatar loaded from absolute path: " + avatarsFile.getAbsolutePath());
                        imageFound = true;
                    } else {
                        File rootFile = new File("src/main/java/poly/cafe/images/" + imageName);
                        if (rootFile.exists()) {
                            XIcon.setIcon(lblImg, rootFile);
                            System.out.println("Avatar loaded from absolute root path: " + rootFile.getAbsolutePath());
                            imageFound = true;
                        }
                    }
                }
            }

            // Nếu không tìm thấy, thử tìm file tương tự trong thư mục avatars
            if (!imageFound) {
                File avatarsDir = new File("src/main/java/poly/cafe/images/avatars");
                if (avatarsDir.exists() && avatarsDir.isDirectory()) {
                    File[] files = avatarsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            // Kiểm tra xem tên file có chứa từ khóa từ tên user không
                            String userName = entity.getFullname();
                            if (userName != null) {
                                userName = userName.toLowerCase();
                                String fileName = file.getName().toLowerCase();

                                // Tìm kiếm theo tên user (username)
                                String username = entity.getUsername();
                                if (username != null && fileName.contains(username.toLowerCase())) {
                                    XIcon.setIcon(lblImg, file);
                                    System.out.println("Found matching avatar by username: " + file.getName()
                                            + " for user: " + username);
                                    imageFound = true;
                                    break;
                                }

                                // Tìm kiếm theo tên đầy đủ (fallback)
                                String[] nameParts = userName.split("\\s+");
                                for (String part : nameParts) {
                                    if (part.length() > 2 && fileName.contains(part)) {
                                        XIcon.setIcon(lblImg, file);
                                        System.out.println("Found similar avatar by name part: " + file.getName()
                                                + " for user: " + userName);
                                        imageFound = true;
                                        break;
                                    }
                                }
                                if (imageFound)
                                    break;
                            }
                        }
                    }
                }
            }

            if (!imageFound) {
                System.out.println("Avatar not found in any location: " + imageName);
                lblImg.setIcon(null);
            }
        } else {
            lblImg.setIcon(null);
            System.out.println("No avatar name provided for user: " + entity.getUsername());
        }

        // Vai trò: chỉ hiển thị lựa chọn Nhân viên trong view này
        // Không auto-chọn Staff khi user là quản lý
        buttonGroup1.clearSelection();
        User.Role role = entity.getRole();
        rdoStaff.setSelected(role == User.Role.staff);
        // Trạng thái
        if (entity.isEnabled()) {
            rdoActive.setSelected(true); // Hoạt động
        } else {
            rdoInactive.setSelected(true); // Tạm dừng
        }
    }

    @Override
    public User getForm() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String fullname = txtFullname.getText().trim();
        String photo = lblImg.getToolTipText();
        if (photo == null)
            photo = "";
        photo = photo.trim();

        // Kiểm tra độ dài tên file để tránh lỗi database
        if (photo.length() > 50) {
            // Cắt tên file nếu quá dài, giữ lại phần mở rộng
            int lastDotIndex = photo.lastIndexOf('.');
            if (lastDotIndex > 0) {
                String name = photo.substring(0, lastDotIndex);
                String extension = photo.substring(lastDotIndex);
                if (name.length() > 45) { // Để lại 5 ký tự cho extension
                    name = name.substring(0, 45);
                }
                photo = name + extension;
            } else {
                photo = photo.substring(0, 50);
            }
            System.out.println("Tên file đã được cắt để phù hợp với database: " + photo);
        }

        if (username.isEmpty()) {
            XDialog.alert("Tên đăng nhập không được để trống!");
            return null;
        }
        if (password.isEmpty()) {
            XDialog.alert("Mật khẩu không được để trống!");
            return null;
        }
        if (fullname.isEmpty()) {
            XDialog.alert("Họ và tên không được để trống!");
            return null;
        }
        if (photo.isEmpty()) {
            XDialog.alert("Ảnh đại diện không được để trống!");
            return null;
        }
        // Không yêu cầu chọn vai trò; mặc định tạo/cập nhật là Nhân viên
        if (!rdoActive.isSelected() && !rdoInactive.isSelected()) {
            XDialog.alert("Vui lòng chọn trạng thái (Hoạt động hoặc Tạm dừng)!");
            return null;
        }

        User entity = new User();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setFullname(fullname);
        entity.setPhoto(photo);
        entity.setEnabled(rdoActive.isSelected());

        // Vai trò: luôn gán Nhân viên trong view này
        entity.setRole(User.Role.staff);

        // Tự động gán ShopID cho user mới được tạo
        if (currentUser != null) {
            entity.setShopId(currentUser.getShopId());
            System.out.println("Setting ShopID for new user: " + currentUser.getShopId());
        } else {
            System.out.println("Warning: currentUser is null, cannot set ShopID");
        }

        return entity;
    }

    @Override
    public void create() {
        User entity = this.getForm();
        if (entity == null)
            return;

        // Kiểm tra quyền tạo user
        if (currentUser == null) {
            XDialog.alert("Không có quyền tạo người dùng!");
            return;
        }

        // Nếu là branch_manager, chỉ được tạo nhân viên (không được tạo quản lý khác)
        if (currentUser.getRole() == User.Role.branch_manager && entity.getRole() == User.Role.branch_manager) {
            XDialog.alert("Quản lý chi nhánh chỉ được tạo nhân viên, không được tạo quản lý khác!");
            return;
        }

        System.out.println("Creating user with ShopID: " + entity.getShopId());
        dao.create(entity);
        this.fillToTable();
        this.clear();

        // Hiển thị thông báo thành công
        String userName = entity.getFullname();
        if (userName != null && !userName.isEmpty()) {
            XDialog.alert("Đã tạo mới người dùng: " + userName);
        } else {
            XDialog.alert("Đã tạo mới người dùng thành công!");
        }
    }

    @Override
    public void update() {
        User entity = this.getForm();
        if (entity == null)
            return;

        // Kiểm tra quyền cập nhật user
        if (currentUser == null) {
            XDialog.alert("Không có quyền cập nhật người dùng!");
            return;
        }

        // Nếu là branch_manager, chỉ được cập nhật nhân viên (không được cập nhật quản
        // lý khác)
        if (currentUser.getRole() == User.Role.branch_manager && entity.getRole() == User.Role.branch_manager) {
            XDialog.alert("Quản lý chi nhánh chỉ được cập nhật nhân viên, không được cập nhật quản lý khác!");
            return;
        }

        dao.update(entity);
        this.fillToTable();

        // Hiển thị thông báo thành công
        String userName = entity.getFullname();
        if (userName != null && !userName.isEmpty()) {
            XDialog.alert("Đã cập nhật thông tin người dùng: " + userName);
        } else {
            XDialog.alert("Đã cập nhật thông tin người dùng thành công!");
        }
    }

    @Override
    public void delete() {
        if (XDialog.confirm("Bạn thực sự muốn xóa?")) {
            // Kiểm tra quyền xóa user
            if (currentUser == null) {
                XDialog.alert("Không có quyền xóa người dùng!");
                return;
            }

            String username = txtUsername.getText();

            // Nếu là branch_manager, không được xóa quản lý khác
            if (currentUser.getRole() == User.Role.branch_manager) {
                // TODO: Kiểm tra role của user bị xóa khi có bảng User
                // Hiện tại tạm thời cho phép xóa
            }

            dao.deleteById(username);
            this.fillToTable();
            this.clear();
        }
    }

    @Override
    public void clear() {
        this.setForm(new User());
        txtConfirmPassword.setText("");
        this.setEditable(false);
        // Reset hình ảnh
        lblImg.setIcon(null);
        lblImg.setToolTipText("");
    }

    @Override
    public void setEditable(boolean editable) {
        // XỬ LÝ PHẦN NÀY
        txtUsername.setEnabled(!editable);

        txtFullname.setEnabled(true);
        txtPassword.setEnabled(true);
        txtConfirmPassword.setEnabled(true);
        rdoActive.setEnabled(true);

        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);

        int rowCount = tblUsers.getRowCount();
        btnMoveFirst.setEnabled(editable && rowCount > 0);
        btnMovePrevious.setEnabled(editable && rowCount > 0);
        btnMoveNext.setEnabled(editable && rowCount > 0);
        btnMoveLast.setEnabled(editable && rowCount > 0);
    }

    @Override
    public void moveFirst() {
        this.moveTo(0);
    }

    @Override
    public void movePrevious() {
        this.moveTo(tblUsers.getSelectedRow() - 1);
    }

    @Override
    public void moveNext() {
        this.moveTo(tblUsers.getSelectedRow() + 1);
    }

    @Override
    public void moveLast() {
        this.moveTo(tblUsers.getRowCount() - 1);
    }

    @Override
    public void moveTo(int index) {
        if (index < 0) {
            this.moveLast();
        } else if (index >= tblUsers.getRowCount()) {
            this.moveFirst();
        } else {
            tblUsers.clearSelection();
            tblUsers.setRowSelectionInterval(index, index);
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFullname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rdoActive = new javax.swing.JRadioButton();
        rdoInactive = new javax.swing.JRadioButton();
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
        rdoStaff = new javax.swing.JRadioButton();
        txtPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý người dùng");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(245, 236, 213));

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null }
                },
                new String[] {
                        "Tên đăng nhập", "Mật khẩu", "Họ và tên", "Hình ảnh", "Vai trò", "Trạng thái", ""
                }) {
            Class[] types = new Class[] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnDeleteCheckedItems,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnUncheckAll, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 667,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCheckAll)
                                        .addComponent(btnUncheckAll)
                                        .addComponent(btnDeleteCheckedItems))
                                .addGap(15, 15, 15)));

        tabs.addTab("DANH SÁCH", jPanel1);

        jPanel2.setBackground(new java.awt.Color(245, 236, 213));

        jLabel1.setText("Họ và tên");

        jLabel2.setText("Trạng thái");

        buttonGroup2.add(rdoActive);
        rdoActive.setText("Hoạt động");

        buttonGroup2.add(rdoInactive);
        rdoInactive.setText("Tạm dừng");

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

        buttonGroup1.add(rdoStaff);
        rdoStaff.setText("Nhân viên");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                .addComponent(txtUsername)
                                                                .addComponent(jLabel5,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel3,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel7)
                                                                .addComponent(txtPassword,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(rdoStaff))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35,
                                                        Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(txtFullname)
                                                        .addComponent(jLabel2)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(rdoActive)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(rdoInactive))
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(txtConfirmPassword,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
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
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(15, 15, 15)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel3))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtFullname,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtUsername,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtPassword,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtConfirmPassword,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel7)
                                                        .addComponent(jLabel2))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(rdoStaff)
                                                        .addComponent(rdoActive)
                                                        .addComponent(rdoInactive)))
                                        .addComponent(lblImg, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 198,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCreate)
                                        .addComponent(btnDelete)
                                        .addComponent(btnMoveFirst)
                                        .addComponent(btnMovePrevious)
                                        .addComponent(btnMoveNext)
                                        .addComponent(btnMoveLast)
                                        .addComponent(btnUpdate)
                                        .addComponent(btnClear))
                                .addGap(15, 15, 15)));

        tabs.addTab("BIỂU MẪU", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 700,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 321,
                                javax.swing.GroupLayout.PREFERRED_SIZE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        try {
            User user = getForm(); // Lấy dữ liệu từ form nhập

            if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
                JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp!");
                return;
            }

            dao.create(user); // Gọi DAO để thêm vào DB
            this.fillToTable(); // Load lại bảng
            JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thêm người dùng thất bại!");
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
        this.clear();
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

    private void btnDeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDeleteCheckedItemsActionPerformed
        // TODO add your handling code here:
        this.deleteCheckedItems();
    }// GEN-LAST:event_btnDeleteCheckedItemsActionPerformed

    private void btnUncheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUncheckAllActionPerformed
        // TODO add your handling code here:
        this.uncheckAll();
    }// GEN-LAST:event_btnUncheckAllActionPerformed

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }// GEN-LAST:event_btnCheckAllActionPerformed

    private void tblUsersMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tblUsersMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.edit();
        }
    }// GEN-LAST:event_tblUsersMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }// GEN-LAST:event_formWindowOpened

    private void lblImgMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lblImgMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) { // Chỉ cần click 1 lần
            this.chooseFile();
        }
    }// GEN-LAST:event_lblImgMouseClicked

    /**
     * Initialize icons for buttons
     */
    private void initIcons() {
        IconUtils.setButtonIconSafe(btnCheckAll, "/poly/cafe/images/icons/list.png", 16, 16);
        IconUtils.setButtonIconSafe(btnUncheckAll, "/poly/cafe/images/icons/refresh.png", 16, 16);
        IconUtils.setButtonIconSafe(btnDeleteCheckedItems, "/poly/cafe/images/icons/delete.png", 16, 16);
        IconUtils.setButtonIconSafe(btnCreate, "/poly/cafe/images/icons/add.png", 16, 16);
        IconUtils.setButtonIconSafe(btnUpdate, "/poly/cafe/images/icons/edit.png", 16, 16);
        IconUtils.setButtonIconSafe(btnDelete, "/poly/cafe/images/icons/delete.png", 16, 16);
        IconUtils.setButtonIconSafe(btnClear, "/poly/cafe/images/icons/refresh.png", 16, 16);
    }

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
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }
        // </editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserManagerJDialog dialog = new UserManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup2;
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
    private javax.swing.JRadioButton rdoActive;
    private javax.swing.JRadioButton rdoInactive;
    private javax.swing.JRadioButton rdoStaff;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblUsers;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtFullname;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
