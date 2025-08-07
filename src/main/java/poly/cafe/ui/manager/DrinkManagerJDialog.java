/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.dao.impl.DrinkDAOImpl;
import poly.cafe.entity.Category;
import poly.cafe.entity.Drink;
import poly.cafe.util.XDialog;
import poly.cafe.util.XIcon;

/**
 *
 * @author wangquockhanh
 */
public class DrinkManagerJDialog extends javax.swing.JDialog implements DrinkManagerController {

    /**
     * Creates new form DrinkManagerJDialog
     * 
     * @param parent
     * @param modal
     */
    public DrinkManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initIcons();
        tblCategories.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillToTable();
            }
        });

        tblDrinks.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Kiểm tra double-click
                    int selectedRow = tblDrinks.rowAtPoint(evt.getPoint());
                    if (selectedRow >= 0) {
                        System.out.println("Double-clicked row in tblDrinks: " + selectedRow);
                        tblDrinks.setRowSelectionInterval(selectedRow, selectedRow);
                        edit();
                    }
                }
            }
        });

        // Không cần thêm MouseListener ở đây vì đã có trong initComponents()
    }

    DrinkDAO dao = new DrinkDAOImpl();
    List<Drink> items = new ArrayList<>(); // đồ uống
    List<Category> categories = new ArrayList<>();
    JFileChooser fileChooser = new JFileChooser();

    @Override
    public void fillCategories() {
        DefaultComboBoxModel cboModel = (DefaultComboBoxModel) cboCategories.getModel();
        cboModel.removeAllElements();

        DefaultTableModel tblModel = (DefaultTableModel) tblCategories.getModel();
        tblModel.setRowCount(0);

        try {
            CategoryDAO cdao = new CategoryDAOImpl();
            categories = cdao.findAll();
            System.out.println("Number of categories: " + categories.size());

            if (categories.isEmpty()) {
                System.out.println("No categories found in database.");
                JOptionPane.showMessageDialog(this, "Không có loại đồ uống nào trong cơ sở dữ liệu!");
            } else {
                categories.forEach(category -> {
                    System.out.println("Adding category: " + category);
                    cboModel.addElement(category);
                    tblModel.addRow(new Object[] { category.getName() });
                });
                tblCategories.setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            System.out.println("Error in fillCategories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void chooseFile() {
        // Kiểm tra xem file có phải từ thư mục drinks không
        File currentDir = new File("src/main/java/poly/cafe/images/drinks");
        if (currentDir.exists()) {
            fileChooser.setCurrentDirectory(currentDir);
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Kiểm tra xem file đã có trong thư mục drinks chưa
            File drinksDir = new File("src/main/java/poly/cafe/images/drinks");
            if (selectedFile.getParentFile().equals(drinksDir)) {
                // File đã có trong thư mục drinks, chỉ cần lưu tên file
                lblImage.setToolTipText(selectedFile.getName());
                XIcon.setIcon(lblImage, selectedFile);
                System.out.println("Image selected from drinks folder: " + selectedFile.getName());
            } else {
                // File từ nơi khác, copy vào thư mục drinks nhưng lưu tên file gốc
                File file = XIcon.copyTo(selectedFile, "images/drinks");
                // Lưu tên file gốc thay vì tên file được tạo bởi copyTo
                lblImage.setToolTipText(selectedFile.getName());
                XIcon.setIcon(lblImage, file);
                System.out.println("Image copied to drinks folder: " + file.getName() + " but saved original name: "
                        + selectedFile.getName());
            }
        }
    }

    /*
     * @Override
     * public void fillToTable() {
     * DefaultTableModel model = (DefaultTableModel) tblDrinks.getModel();
     * model.setRowCount(0);
     * System.out.println("Filling tblDrinks, model row count set to 0");
     * 
     * int selectedRow = tblCategories.getSelectedRow();
     * if (selectedRow >= 0 && !categories.isEmpty()) {
     * Category category = categories.get(selectedRow);
     * System.out.println("Selected CategoryId: " + category.getId());
     * items = dao.findByCategoryId(category.getId());
     * System.out.println("Number of items for CategoryId " + category.getId() +
     * ": " + items.size());
     * items.forEach(item -> {
     * Object[] rowData = {
     * item.getId(),
     * item.getName(),
     * item.getUnitPrice(),
     * item.getDiscount(),
     * item.isAvailable() ? "Sẵn có" : "Hết hàng",
     * false
     * };
     * model.addRow(rowData);
     * System.out.println("Added to table: " + item.getId() + " - " +
     * item.getName());
     * });
     * } else {
     * System.out.println("No category selected or categories list is empty.");
     * }
     * // Loại bỏ clear() ở đây để tránh làm mất dữ liệu vừa thêm
     * }
     */

    @Override
    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblDrinks.getModel();
        model.setRowCount(0); // Xóa các dòng cũ

        // 1. Sử dụng cách kiểm tra an toàn hơn
        int selectedRow = tblCategories.getSelectedRow();
        if (selectedRow >= 0 && !categories.isEmpty()) {
            Category category = categories.get(selectedRow);

            // 2. Cập nhật biến items để có thể edit
            items = dao.findByCategoryId(category.getId());

            items.forEach(item -> {
                Object[] rowData = {
                        item.getId(),
                        item.getName(),
                        // 3. Định dạng tiền tệ chuyên nghiệp hơn
                        String.format("%,.0f VNĐ", item.getUnitPrice()),
                        // 3. Định dạng phần trăm
                        String.format("%.0f%%", item.getDiscount()),
                        item.isAvailable() ? "Sẵn có" : "Hết hàng",
                        false
                };
                model.addRow(rowData);
            });
        }
        // 4. Loại bỏ lệnh this.clear() để tránh lỗi logic
    }

    @Override
    public void open() {
        this.setLocationRelativeTo(null);
        this.fillCategories();
        if (!categories.isEmpty()) {
            this.fillToTable();
        }
        this.clear();
    }

    @Override
    public void edit() {
        Drink entity = items.get(tblDrinks.getSelectedRow());
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
        for (int i = 0; i < tblDrinks.getRowCount(); i++) {
            tblDrinks.setValueAt(checked, i, 5);
        }
    }

    @Override
    public void deleteCheckedItems() {
        if (XDialog.confirm("Bạn thực sự muốn xóa các mục chọn?")) {
            for (int i = 0; i < tblDrinks.getRowCount(); i++) {
                if ((Boolean) tblDrinks.getValueAt(i, 5)) {
                    dao.deleteById(items.get(i).getId());
                }
            }
            this.fillToTable();
        }
    }

    @Override
    public void setForm(Drink entity) {
        txtId.setText(entity.getId());
        txtName.setText(entity.getName());
        txtUnitPrice.setText(String.valueOf(entity.getUnitPrice()));
        // Slider
        sldDiscount.setValue((int) entity.getDiscount());
        // Combobox
        for (int i = 0; i < cboCategories.getItemCount(); i++) {
            Object obj = cboCategories.getItemAt(i);
            if (obj instanceof Category) {
                Category cate = (Category) obj;
                if (cate.getId().equals(entity.getCategoryId())) {
                    cboCategories.setSelectedItem(cate);
                    break;
                }
            }
        }
        // Img
        String imageName = entity.getImage();
        lblImage.setToolTipText(imageName);
        if (imageName != null && !imageName.isEmpty()) {
            boolean imageFound = false;

            // Thử tìm trong thư mục drinks trước
            String drinksImagePath = "/poly/cafe/images/drinks/" + imageName;
            try {
                XIcon.setIcon(lblImage, drinksImagePath);
                System.out.println("Image loaded from drinks folder: " + drinksImagePath);
                imageFound = true;
            } catch (Exception e) {
                // Nếu không tìm thấy trong drinks, thử tìm trong thư mục gốc images
                String rootImagePath = "/poly/cafe/images/" + imageName;
                try {
                    XIcon.setIcon(lblImage, rootImagePath);
                    System.out.println("Image loaded from root images folder: " + rootImagePath);
                    imageFound = true;
                } catch (Exception e2) {
                    // Thử tìm bằng đường dẫn tuyệt đối
                    File drinksFile = new File("src/main/java/poly/cafe/images/drinks/" + imageName);
                    if (drinksFile.exists()) {
                        XIcon.setIcon(lblImage, drinksFile);
                        System.out.println("Image loaded from absolute path: " + drinksFile.getAbsolutePath());
                        imageFound = true;
                    } else {
                        File rootFile = new File("src/main/java/poly/cafe/images/" + imageName);
                        if (rootFile.exists()) {
                            XIcon.setIcon(lblImage, rootFile);
                            System.out.println("Image loaded from absolute root path: " + rootFile.getAbsolutePath());
                            imageFound = true;
                        }
                    }
                }
            }

            // Nếu không tìm thấy, thử tìm file tương tự trong thư mục drinks
            if (!imageFound) {
                File drinksDir = new File("src/main/java/poly/cafe/images/drinks");
                if (drinksDir.exists() && drinksDir.isDirectory()) {
                    File[] files = drinksDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            // Kiểm tra xem tên file có chứa từ khóa từ tên đồ uống không
                            String drinkName = entity.getName();
                            if (drinkName != null) {
                                drinkName = drinkName.toLowerCase();
                                String fileName = file.getName().toLowerCase();

                                // Tìm kiếm theo tên đồ uống (CAT1_, CAT2_, etc.)
                                String drinkId = entity.getId();
                                if (drinkId != null && drinkId.startsWith("CAT")) {
                                    // Lấy phần category từ ID (CAT1, CAT2, etc.)
                                    String category = drinkId.substring(0, 4); // CAT1, CAT2, etc.
                                    if (fileName.startsWith(category.toLowerCase())) {
                                        XIcon.setIcon(lblImage, file);
                                        System.out.println("Found matching image by category: " + file.getName()
                                                + " for drink: " + drinkId);
                                        imageFound = true;
                                        break;
                                    }
                                }

                                // Tìm kiếm theo từ khóa (fallback)
                                if (drinkName.contains("cà phê") && fileName.contains("caphe")) {
                                    XIcon.setIcon(lblImage, file);
                                    System.out.println("Found similar image for coffee: " + file.getName());
                                    imageFound = true;
                                    break;
                                } else if (drinkName.contains("trà") && fileName.contains("tra")) {
                                    XIcon.setIcon(lblImage, file);
                                    System.out.println("Found similar image for tea: " + file.getName());
                                    imageFound = true;
                                    break;
                                } else if (drinkName.contains("nước ép") && fileName.contains("nuocep")) {
                                    XIcon.setIcon(lblImage, file);
                                    System.out.println("Found similar image for juice: " + file.getName());
                                    imageFound = true;
                                    break;
                                } else if (drinkName.contains("sinh tố") && fileName.contains("sinhto")) {
                                    XIcon.setIcon(lblImage, file);
                                    System.out.println("Found similar image for smoothie: " + file.getName());
                                    imageFound = true;
                                    break;
                                } else if (drinkName.contains("soda") && fileName.contains("soda")) {
                                    XIcon.setIcon(lblImage, file);
                                    System.out.println("Found similar image for soda: " + file.getName());
                                    imageFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!imageFound) {
                System.out.println("Image not found in any location: " + imageName);
                lblImage.setIcon(null);
            }
        } else {
            lblImage.setIcon(null);
            System.out.println("No image name provided for entity: " + entity.getId());
        }
        // Trạng thái
        if (entity.isAvailable()) {
            rdoAvailable.setSelected(true); // Quản lý
        } else {
            rdoOutOfStock.setSelected(true); // Nhân viên
        }
    }

    @Override
    public Drink getForm() {
        Drink entity = new Drink();
        entity.setId(txtId.getText());
        entity.setName(txtName.getText());
        try {
            double price = Double.parseDouble(txtUnitPrice.getText());
            entity.setUnitPrice(price);
        } catch (NumberFormatException e) {
            entity.setUnitPrice(0);
        }
        // Slider
        entity.setDiscount(sldDiscount.getValue());
        // Img
        entity.setImage(lblImage.getToolTipText());
        // Combobox
        Category selected = (Category) cboCategories.getSelectedItem();
        if (selected != null) {
            entity.setCategoryId(selected.getId());
        }
        // Trạng thái
        entity.setAvailable(rdoAvailable.isSelected());
        return entity;
    }

    @Override
    public void create() {
        Drink entity = this.getForm();
        dao.create(entity);
        this.fillToTable();
        this.clear();

        // Hiển thị thông báo thành công
        String drinkName = entity.getName();
        if (drinkName != null && !drinkName.isEmpty()) {
            XDialog.alert("Đã tạo mới sản phẩm: " + drinkName);
        } else {
            XDialog.alert("Đã tạo mới sản phẩm thành công!");
        }
    }

    @Override
    public void update() {
        Drink entity = this.getForm();
        dao.update(entity);
        this.fillToTable();

        // Hiển thị thông báo thành công
        String drinkName = entity.getName();
        if (drinkName != null && !drinkName.isEmpty()) {
            XDialog.alert("Đã cập nhật thông tin sản phẩm: " + drinkName);
        } else {
            XDialog.alert("Đã cập nhật thông tin sản phẩm thành công!");
        }
    }

    @Override
    public void delete() {
        if (XDialog.confirm("Bạn thực sự muốn xóa?")) {
            String username = txtId.getText();
            dao.deleteById(username);
            this.fillToTable();
            this.clear();
        }
    }

    @Override
    public void clear() {
        this.setForm(new Drink());
        this.setEditable(false);
    }

    @Override
    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);

        txtName.setEnabled(true);
        txtUnitPrice.setEnabled(true);
        sldDiscount.setEnabled(true);
        cboCategories.setEnabled(true);
        rdoAvailable.setEnabled(true);

        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);

        int rowCount = tblDrinks.getRowCount();
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
        this.moveTo(tblDrinks.getSelectedRow() - 1);
    }

    @Override
    public void moveNext() {
        this.moveTo(tblDrinks.getSelectedRow() + 1);
    }

    @Override
    public void moveLast() {
        this.moveTo(tblDrinks.getRowCount() - 1);
    }

    @Override
    public void moveTo(int index) {
        if (index < 0) {
            this.moveLast();
        } else if (index >= tblDrinks.getRowCount()) {
            this.moveFirst();
        } else {
            tblDrinks.clearSelection();
            tblDrinks.setRowSelectionInterval(index, index);
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

        jRadioButton2 = new javax.swing.JRadioButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDrinks = new javax.swing.JTable();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategories = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtUnitPrice = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        sldDiscount = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        cboCategories = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        rdoAvailable = new javax.swing.JRadioButton();
        rdoOutOfStock = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();

        jRadioButton2.setText("jRadioButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý đồ uống");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        tabs.setBackground(new java.awt.Color(245, 236, 213));

        jPanel2.setBackground(new java.awt.Color(245, 236, 213));

        tblDrinks.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null }
                },
                new String[] {
                        "Mã đồ uống", "Tên đồ uống", "Đơn giá", "Giảm giá", "Trạng thái", ""
                }) {
            Class[] types = new Class[] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        tblDrinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDrinksMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblDrinks);

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

        tblCategories.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null },
                        { null },
                        { null },
                        { null }
                },
                new String[] {
                        "Loại đồ uống"
                }));
        jScrollPane1.setViewportView(tblCategories);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnDeleteCheckedItems,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnUncheckAll, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnCheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 144,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(15, 15, 15))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(15, 15, 15)
                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 513,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(15, 15, 15)))));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198,
                                                Short.MAX_VALUE)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                Short.MAX_VALUE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnCheckAll)
                                        .addComponent(btnUncheckAll)
                                        .addComponent(btnDeleteCheckedItems))
                                .addGap(15, 15, 15)));

        tabs.addTab("DANH SÁCH", jPanel2);

        jPanel3.setBackground(new java.awt.Color(245, 236, 213));

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("HÌNH");
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Mã đồ uống");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Tên đồ uống");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Đơn giá");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Giảm giá");

        sldDiscount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        sldDiscount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldDiscountStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Loại");

        cboCategories.setToolTipText("");
        cboCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoriesActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Trạng thái");

        buttonGroup1.add(rdoAvailable);
        rdoAvailable.setText("Sẵn có");

        buttonGroup1.add(rdoOutOfStock);
        rdoOutOfStock.setText("Hết hàng");

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(txtName)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                200, Short.MAX_VALUE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(txtId)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(txtUnitPrice))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32,
                                                        Short.MAX_VALUE)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel3Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                .addComponent(jLabel5,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(sldDiscount,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel6,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel3Layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel7,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 125,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cboCategories,
                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 206,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                        .addComponent(rdoAvailable)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(rdoOutOfStock)))))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
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
                                .addGap(15, 15, 15))
                        .addComponent(jSeparator1));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel5))
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(sldDiscount,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel6))
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cboCategories,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel7))
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel3Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtUnitPrice,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(rdoAvailable)
                                                        .addComponent(rdoOutOfStock)))
                                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 180,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDelete)
                                        .addComponent(btnMoveFirst)
                                        .addComponent(btnMovePrevious)
                                        .addComponent(btnMoveNext)
                                        .addComponent(btnMoveLast)
                                        .addComponent(btnUpdate)
                                        .addComponent(btnClear)
                                        .addComponent(btnCreate))
                                .addGap(15, 15, 15)));

        tabs.addTab("BIỂU MẪU", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 700,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabs));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        this.create();
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        open();
    }// GEN-LAST:event_formWindowOpened

    private void tblDrinksMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tblDrinksMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) { // Có thể thêm điều kiện click đúp
            this.edit(); // Thêm dòng này
        } // GEN-LAST:event_tblDrinksMouseClicked
    }

    private void sldDiscountStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_sldDiscountStateChanged
        // TODO add your handling code here:

    }// GEN-LAST:event_sldDiscountStateChanged

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lblImageMouseClicked
        // TODO add your handling code here:
        this.chooseFile();
    }// GEN-LAST:event_lblImageMouseClicked

    private void cboCategoriesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cboCategoriesActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_cboCategoriesActionPerformed

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
            java.util.logging.Logger.getLogger(DrinkManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DrinkManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DrinkManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DrinkManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }
        // </editor-fold>
        // System.out.println("Number of categories: " + categories.size());
        // categories.forEach(category -> System.out.println("Category: " +
        // category.getName()));

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DrinkManagerJDialog dialog = new DrinkManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUncheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboCategories;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JRadioButton rdoAvailable;
    private javax.swing.JRadioButton rdoOutOfStock;
    private javax.swing.JSlider sldDiscount;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblCategories;
    private javax.swing.JTable tblDrinks;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
