/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import javax.swing.Timer;
import javax.swing.UIManager;
import poly.cafe.entity.User;
import poly.cafe.util.XAuth;

/**
 *
 * @author wangquockhanh
 */
public final class PolyCafeJFrame extends javax.swing.JFrame implements PolyCafeController {

    /**
     * Creates new form ChangePasswordJDialog
     */
    public PolyCafeJFrame() {
        initComponents();
        this.init();
    }

    @Override
    public void init() {
        // Kiểm tra xem XAuth.user có tồn tại không
        if (XAuth.user == null) {
            System.err.println("XAuth.user is null! Không thể khởi tạo giao diện.");
            return;
        }

        // Đảm bảo frame hiển thị ở trên cùng
        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();

        // Đường dẫn tuyệt đối cho máy local
        String urlCoffeeLogo = "file:/C:/Users/Tan Phat Computer Q8/Fpoly_DA1_Nhom1_PolyCafe/src/main/java/poly/cafe/images/icons/coffee.png";

        // Fallback cho các máy remote khác - sử dụng đường dẫn tương đối từ resource
        String fallbackCoffeeLogo = "/poly/cafe/images/icons/coffee.png";

        this.setLocationRelativeTo(null);

        // Hiển thị logo coffee trong lbLogoName với nhiều đường dẫn fallback và scale đúng kích thước
        try {
            javax.swing.ImageIcon logoIcon = null;
            // Thứ tự ưu tiên các path
            String[] candidatePaths = new String[] {
                "/poly/cafe/images/icons/coffee.png",         // resource icons (ưu tiên)
                "/poly/cafe/images/logo/coffee.png",          // resource logo/coffee.png
                "/poly/cafe/images/logo/logo.png",            // resource logo.png
                "/poly/cafe/images/logo/logo.jpg"             // resource logo.jpg (hiện có)
            };

            for (String p : candidatePaths) {
                java.net.URL url = getClass().getResource(p);
                if (url != null) {
                    logoIcon = new javax.swing.ImageIcon(url);
                    break;
                }
            }

            // Fallback cuối: thử theo đường dẫn file tương đối trong project
            if (logoIcon == null) {
                java.io.File f1 = new java.io.File("src/main/java/poly/cafe/images/icons/coffee.png");
                java.io.File f2 = new java.io.File("src/main/java/poly/cafe/images/logo/coffee.png");
                java.io.File f3 = new java.io.File("src/main/java/poly/cafe/images/logo/logo.png");
                java.io.File f4 = new java.io.File("src/main/java/poly/cafe/images/logo/logo.jpg");
                java.io.File[] files = new java.io.File[] { f1, f2, f3, f4 };
                for (java.io.File f : files) {
                    if (f.exists()) {
                        logoIcon = new javax.swing.ImageIcon(f.getAbsolutePath());
                        break;
                    }
                }
            }

            if (logoIcon != null && logoIcon.getImage() != null) {
                java.awt.Image scaledLogo = logoIcon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
                lbLogoName.setIcon(new javax.swing.ImageIcon(scaledLogo));
            } else {
                System.err.println("Không tìm thấy file/logo coffee, hiển thị text mặc định.");
            }
        } catch (Exception e) {
            System.err.println("Không thể load logo: " + e.getMessage());
        }
        lbLogoName.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lbLogoName.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        lbLogoName.setIconTextGap(10); // Khoảng cách giữa icon và text

        // Thêm background cho backgroundManager và scale theo kích thước
        String fallbackBackground = "/poly/cafe/images/backgrounds/coffee-shop.jpg";
        System.out.println("Đang load background ban đầu: " + fallbackBackground);
        System.out.println("backgroundManager object: " + backgroundManager);
        System.out.println("backgroundManager size: " + backgroundManager.getSize());
        System.out.println("backgroundManager bounds: " + backgroundManager.getBounds());
        System.out.println("backgroundManager parent: " + backgroundManager.getParent());
        System.out.println("backgroundManager visible: " + backgroundManager.isVisible());
        System.out.println("backgroundManager opaque: " + backgroundManager.isOpaque());

        // Kiểm tra xem có component nào đang che phủ backgroundManager không
        if (backgroundManager.getParent() != null) {
            java.awt.Container parent = backgroundManager.getParent();
            System.out.println("Parent component: " + parent);
            System.out.println("Parent bounds: " + parent.getBounds());
            System.out.println("Parent visible: " + parent.isVisible());

            // Kiểm tra các component con của parent
            java.awt.Component[] children = parent.getComponents();
            System.out.println("Số component con: " + children.length);
            for (int i = 0; i < children.length; i++) {
                java.awt.Component child = children[i];
                System.out.println("Child " + i + ": " + child.getClass().getSimpleName() +
                        " - bounds: " + child.getBounds() +
                        " - visible: " + child.isVisible() +
                        " - name: " + child.getName());
            }

            // Kiểm tra xem backgroundManager có phải là component cuối cùng không
            if (children.length > 0) {
                java.awt.Component lastChild = children[children.length - 1];
                System.out.println("Last child: " + lastChild.getClass().getSimpleName() +
                        " - name: " + lastChild.getName() +
                        " - is backgroundManager: " + (lastChild == backgroundManager));
            }
        }

        // Kiểm tra pnlManager
        System.out.println("pnlManager bounds: " + pnlManager.getBounds());
        System.out.println("pnlManager background: " + pnlManager.getBackground());
        System.out.println("pnlManager opaque: " + pnlManager.isOpaque());

        try {
            // Thử load từ resource path trước
            javax.swing.ImageIcon backgroundIcon = null;
            java.net.URL backgroundUrl = getClass().getResource(fallbackBackground);

            if (backgroundUrl != null) {
                System.out.println("Tìm thấy resource background: " + backgroundUrl.toString());
                backgroundIcon = new javax.swing.ImageIcon(backgroundUrl);
            } else {
                System.out.println("Resource path không hoạt động, thử file path...");
                // Thử load từ file path
                String filePath = "src/main/java/poly/cafe/images/backgrounds/coffee-shop.jpg";
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    System.out.println("Tìm thấy file background: " + file.getAbsolutePath());
                    backgroundIcon = new javax.swing.ImageIcon(file.getAbsolutePath());
                } else {
                    System.err.println("File background không tồn tại: " + file.getAbsolutePath());
                }
            }

            // Kiểm tra và scale image nếu load thành công
            if (backgroundIcon != null && backgroundIcon.getImage() != null) {
                // Scale image theo kích thước của backgroundManager (740x550)
                java.awt.Image scaledBackground = backgroundIcon.getImage().getScaledInstance(740, 550,
                        java.awt.Image.SCALE_SMOOTH);
                javax.swing.ImageIcon scaledIcon = new javax.swing.ImageIcon(scaledBackground);
                backgroundManager.setIcon(scaledIcon);
                System.out.println("Đã load background ban đầu thành công!");
                System.out.println("Background icon set: " + (backgroundManager.getIcon() != null));
                System.out.println("Background visible: " + backgroundManager.isVisible());

                // Đảm bảo backgroundManager được vẽ lại
                backgroundManager.revalidate();
                backgroundManager.repaint();

                // Đảm bảo backgroundManager có độ ưu tiên cao nhất
                backgroundManager.setOpaque(false);
                backgroundManager.setBackground(new java.awt.Color(0, 0, 0, 0)); // Transparent background

                // Đảm bảo backgroundManager hiển thị đúng
                backgroundManager.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                backgroundManager.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

                // Đảm bảo backgroundManager không bị che phủ
                if (backgroundManager.getParent() != null) {
                    backgroundManager.getParent().setComponentZOrder(backgroundManager, 0);
                }

                // Làm cho pnlManager trong suốt để background hiển thị
                pnlManager.setOpaque(false);
                pnlManager.setBackground(new java.awt.Color(0, 0, 0, 0));

                // Force repaint toàn bộ frame
                this.getContentPane().revalidate();
                this.getContentPane().repaint();
            } else {
                System.err.println("Không thể load được background ban đầu");
                System.err.println("Resource URL: " + (backgroundUrl != null ? backgroundUrl.toString() : "null"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load background ban đầu: " + e.getMessage());
            e.printStackTrace();
        }

        // Khởi tạo đồng hồ 24 giờ
        initClock();

        // Khởi tạo nút thay đổi background
        initBackgroundButton();

        System.out.println("User: " + XAuth.user.getFullname() + ", Role: " + XAuth.user.getRole());

        User.Role role = XAuth.user.getRole();
        // Phân quyền
        if (role == User.Role.staff) {
            // Nhân viên - chỉ thấy 4 nút cơ bản, ẩn các nút quản lý nhưng giữ nguyên kích
            // thước
            btnDrinkManager.setEnabled(false);
            btnCategoryManager.setEnabled(false);
            btnCardManager.setEnabled(false);
            btnBillManager.setEnabled(false);
            btnUserManager.setEnabled(false);
            btnRevenueManager.setEnabled(false);

            // Làm cho các nút trông như bị ẩn bằng cách thay đổi màu sắc và opacity
            btnDrinkManager.setBackground(new java.awt.Color(200, 200, 200));
            btnCategoryManager.setBackground(new java.awt.Color(200, 200, 200));
            btnCardManager.setBackground(new java.awt.Color(200, 200, 200));
            btnBillManager.setBackground(new java.awt.Color(200, 200, 200));
            btnUserManager.setBackground(new java.awt.Color(200, 200, 200));
            btnRevenueManager.setBackground(new java.awt.Color(200, 200, 200));

            // Thay đổi màu text để làm cho nút trông như bị ẩn
            btnDrinkManager.setForeground(new java.awt.Color(150, 150, 150));
            btnCategoryManager.setForeground(new java.awt.Color(150, 150, 150));
            btnCardManager.setForeground(new java.awt.Color(150, 150, 150));
            btnBillManager.setForeground(new java.awt.Color(150, 150, 150));
            btnUserManager.setForeground(new java.awt.Color(150, 150, 150));
            btnRevenueManager.setForeground(new java.awt.Color(150, 150, 150));
        } else if (role == User.Role.branch_manager) {
            System.out.println("Quản lý chi nhánh - hiển thị 1 phần chức năng");
            btnDrinkManager.setEnabled(true);
            btnCategoryManager.setEnabled(true);
            btnCardManager.setEnabled(true);
            btnBillManager.setEnabled(true);
            btnUserManager.setEnabled(true);
            btnRevenueManager.setEnabled(true);
        }

        pnlManager.revalidate(); // Cập nhật layout
        pnlManager.repaint(); // Vẽ lại

        // Đảm bảo toàn bộ frame được vẽ lại
        this.revalidate();
        this.repaint();

        // Đảm bảo frame hiển thị ở trên cùng sau khi khởi tạo xong
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Đảm bảo frame hiển thị ở trên cùng
                setAlwaysOnTop(false); // Tắt always on top sau khi đã focus
                toFront();
                requestFocus();
                setVisible(true);
            }
        });

        // Thêm một delay nhỏ để đảm bảo UI được khởi tạo hoàn toàn
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("SwingUtilities.invokeLater - kiểm tra lại background");
                if (backgroundManager.getIcon() == null) {
                    System.out.println("Background vẫn null, thử load lại...");
                    // Thử load lại background nếu vẫn null
                    try {
                        String fallbackBackground = "/poly/cafe/images/backgrounds/coffee-shop.jpg";
                        java.net.URL backgroundUrl = getClass().getResource(fallbackBackground);
                        if (backgroundUrl != null) {
                            javax.swing.ImageIcon backgroundIcon = new javax.swing.ImageIcon(backgroundUrl);
                            java.awt.Image scaledBackground = backgroundIcon.getImage().getScaledInstance(740, 550,
                                    java.awt.Image.SCALE_SMOOTH);
                            javax.swing.ImageIcon scaledIcon = new javax.swing.ImageIcon(scaledBackground);
                            backgroundManager.setIcon(scaledIcon);
                            backgroundManager.revalidate();
                            backgroundManager.repaint();
                            System.out.println("Đã load lại background thành công trong invokeLater!");
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi load lại background: " + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void exit() {
        if (poly.cafe.util.XDialog.confirm("Bạn muốn đăng xuất?")) {
            try {
                // Đóng frame hiện tại
                this.dispose();
            } finally {
                // Mở lại màn hình đăng nhập
                javax.swing.SwingUtilities.invokeLater(() -> {
                    LoginJDialog login = new LoginJDialog(null, true);
                    login.setVisible(true);
                });
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLabel2 = new javax.swing.JLabel();
        pnlManager = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnSales = new javax.swing.JButton();
        btnChangePassword = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnDrinkManager = new javax.swing.JButton();
        btnCategoryManager = new javax.swing.JButton();
        btnCardManager = new javax.swing.JButton();
        btnBillManager = new javax.swing.JButton();
        btnUserManager = new javax.swing.JButton();
        btnRevenueManager = new javax.swing.JButton();
        lbLogoName = new javax.swing.JLabel();
        backgroundManager = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
                jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE));
        jFrame1Layout.setVerticalGroup(
                jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE));

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
                jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE));
        jFrame2Layout.setVerticalGroup(
                jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE));

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE));
        jInternalFrame1Layout.setVerticalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE));

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Trang Chủ - HighBUG Coffee");
        setIconImage(getIconImage());
        setIconImages(getIconImages());
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlManager.setBackground(new java.awt.Color(255, 255, 255));
        pnlManager.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(245, 236, 213));

        btnSales.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnSales.setText("BÁN HÀNG");
        btnSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalesActionPerformed(evt);
            }
        });

        btnChangePassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnChangePassword.setText("ĐỔI MẬT KHẨU");
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
            }
        });

        btnHistory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnHistory.setText("LỊCH SỬ");
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
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

        btnDrinkManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDrinkManager.setText("ĐỒ UỐNG");
        btnDrinkManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrinkManagerActionPerformed(evt);
            }
        });

        btnCategoryManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCategoryManager.setText("LOẠI ĐỒ UỐNG");
        btnCategoryManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryManagerActionPerformed(evt);
            }
        });

        btnCardManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCardManager.setText("THẺ ĐỊNH DANH");
        btnCardManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCardManagerActionPerformed(evt);
            }
        });

        btnBillManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBillManager.setText("PHIẾU BÁN HÀNG");
        btnBillManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBillManagerActionPerformed(evt);
            }
        });

        btnUserManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUserManager.setText("QUẢN LÝ NHÂN VIÊN");
        btnUserManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserManagerActionPerformed(evt);
            }
        });

        btnRevenueManager.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRevenueManager.setText("DOANH THU");
        btnRevenueManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevenueManagerActionPerformed(evt);
            }
        });

        lbLogoName.setBackground(new java.awt.Color(255, 255, 255));
        lbLogoName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbLogoName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbLogoName.setText("HighBUG");
        lbLogoName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        lbLogoName.setOpaque(true);

        // Khởi tạo đồng hồ
        lblClock = new javax.swing.JLabel();
        lblClock.setBackground(new java.awt.Color(0, 0, 0, 0)); // Không có background
        lblClock.setFont(new java.awt.Font("Segoe UI", 1, 54)); // Font size tăng thêm 1.5 lần
        lblClock.setForeground(new java.awt.Color(0, 0, 139)); // Màu xanh dương đậm
        lblClock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClock.setText("00:00");
        lblClock.setOpaque(false); // Không hiển thị background

        // Khởi tạo nút thay đổi background
        btnSettings = new javax.swing.JButton();
        btnSettings.setBackground(new java.awt.Color(70, 130, 180));
        btnSettings.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnSettings.setForeground(java.awt.Color.WHITE);
        btnSettings.setText("Thay đổi BG");
        btnSettings.setFocusPainted(false);
        btnSettings.setBorderPainted(false);
        // Thêm padding cho button để text không bị sát mép
        btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        // Thiết lập kích thước cố định cho nút
        btnSettings.setPreferredSize(new java.awt.Dimension(140, 35));
        btnSettings.setMinimumSize(new java.awt.Dimension(140, 35));
        btnSettings.setMaximumSize(new java.awt.Dimension(140, 35));
        // Đảm bảo text được hiển thị đầy đủ
        btnSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSettings.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnRevenueManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnUserManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                                        .addComponent(btnBillManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCardManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCategoryManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnDrinkManager, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnHistory, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnChangePassword, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnSales, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lbLogoName, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(16, 16, 16)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(lbLogoName, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(btnSales, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDrinkManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCategoryManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCardManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBillManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUserManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRevenueManager, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)));

        pnlManager.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 550));

        backgroundManager.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        backgroundManager.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Thêm đồng hồ vào backgroundManager
        backgroundManager.add(lblClock, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, 240, 90));

        // Thêm nút thay đổi background vào backgroundManager
        backgroundManager.add(btnSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 500, 140, 35));

        pnlManager.add(backgroundManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 740, 550));
        pnlManager.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 160, -1, -1));

        getContentPane().add(pnlManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 940, 550));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRevenueManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRevenueManagerActionPerformed
        // TODO add your handling code here:
        this.showRevenueManagerJDialog(this);
    }// GEN-LAST:event_btnRevenueManagerActionPerformed

    private void btnUserManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnUserManagerActionPerformed
        // TODO add your handling code here:
        this.showUserManagerJDialog(this);
    }// GEN-LAST:event_btnUserManagerActionPerformed

    private void btnBillManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBillManagerActionPerformed
        // TODO add your handling code here:
        this.showBillManagerJDialog(this);
    }// GEN-LAST:event_btnBillManagerActionPerformed

    private void btnCardManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCardManagerActionPerformed
        // TODO add your handling code here:
        this.showCardManagerJDialog(this);
    }// GEN-LAST:event_btnCardManagerActionPerformed

    private void btnCategoryManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCategoryManagerActionPerformed
        // TODO add your handling code here:
        this.showCategoryManagerJDialog(this);
    }// GEN-LAST:event_btnCategoryManagerActionPerformed

    private void btnDrinkManagerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnDrinkManagerActionPerformed
        // TODO add your handling code here:
        this.showDrinkManagerJDialog(this);
    }// GEN-LAST:event_btnDrinkManagerActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        exit();
    }// GEN-LAST:event_btnExitActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHistoryActionPerformed
        // TODO add your handling code here:
        this.showHistoryJDialog(this);
    }// GEN-LAST:event_btnHistoryActionPerformed

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnChangePasswordActionPerformed
        // TODO add your handling code here:
        this.showChangePasswordJDialog(jFrame1);
    }// GEN-LAST:event_btnChangePasswordActionPerformed

    private void btnSalesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSalesActionPerformed
        // TODO add your handling code here:
        this.showSalesJDialog(this);
    }// GEN-LAST:event_btnSalesActionPerformed

    /**
     * Khởi tạo đồng hồ 24 giờ
     */
    private void initClock() {
        // Tạo timer để cập nhật đồng hồ mỗi giây
        clockTimer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateClock();
            }
        });
        clockTimer.start();

        // Tạo timer để nhấp nháy dấu ":" mỗi giây
        blinkTimer = new javax.swing.Timer(500, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blinkColon();
            }
        });
        blinkTimer.start();
    }

    /**
     * Cập nhật thời gian hiển thị
     */
    private void updateClock() {
        LocalTime now = LocalTime.now();
        if (showColon) {
            String timeString = String.format("%02d:%02d", now.getHour(), now.getMinute());
            lblClock.setText(timeString);
        } else {
            String timeString = String.format("%02d %02d", now.getHour(), now.getMinute());
            lblClock.setText(timeString);
        }
    }

    /**
     * Nhấp nháy dấu ":" trong đồng hồ
     */
    private void blinkColon() {
        showColon = !showColon; // Đảo ngược trạng thái
        LocalTime now = LocalTime.now();
        if (showColon) {
            String timeString = String.format("%02d:%02d", now.getHour(), now.getMinute());
            lblClock.setText(timeString);
        } else {
            String timeString = String.format("%02d %02d", now.getHour(), now.getMinute());
            lblClock.setText(timeString);
        }
    }

    /**
     * Khởi tạo nút thay đổi background
     */
    private void initBackgroundButton() {
        // Thiết lập nút để thay đổi background
        btnSettings.setText("Thay đổi BG");
        btnSettings.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnSettings.setForeground(new java.awt.Color(255, 255, 255));
        btnSettings.setBackground(new java.awt.Color(70, 130, 180));
        // Thêm padding cho button để text không bị sát mép
        btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Thiết lập kích thước cho nút - tăng width để hiển thị đủ text
        btnSettings.setPreferredSize(new java.awt.Dimension(140, 35));
        btnSettings.setSize(new java.awt.Dimension(140, 35));

        // Thêm action listener để thay đổi background
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleBackground();
            }
        });

        // Khởi tạo timer cho hiệu ứng thay đổi background
        backgroundTimer = new javax.swing.Timer(100, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animateBackgroundChange();
            }
        });
    }

    /**
     * Chuyển đổi background
     */
    private void toggleBackground() {
        if (!isBackgroundChanged) {
            // Thay đổi background thành coffee-chain-manager
            backgroundTimer.start();
        } else {
            // Khôi phục background gốc (coffee-shop.jpg)
            try {
                System.out.println("Đang khôi phục background gốc: coffee-shop.jpg");

                javax.swing.ImageIcon backgroundIcon = null;
                java.net.URL resourceUrl = getClass().getResource("/poly/cafe/images/backgrounds/coffee-shop.jpg");

                if (resourceUrl != null) {
                    System.out.println("Tìm thấy resource gốc: " + resourceUrl.toString());
                    backgroundIcon = new javax.swing.ImageIcon(resourceUrl);
                } else {
                    System.out.println("Resource path gốc không hoạt động, thử file path...");
                    // Thử load từ file path
                    String filePath = "src/main/java/poly/cafe/images/backgrounds/coffee-shop.jpg";
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        System.out.println("Tìm thấy file gốc: " + file.getAbsolutePath());
                        backgroundIcon = new javax.swing.ImageIcon(file.getAbsolutePath());
                    } else {
                        System.err.println("File gốc không tồn tại: " + file.getAbsolutePath());
                    }
                }

                // Kiểm tra và scale image nếu load thành công
                if (backgroundIcon != null && backgroundIcon.getImage() != null) {
                    // Scale image theo kích thước của backgroundManager
                    java.awt.Image scaledImage = backgroundIcon.getImage().getScaledInstance(740, 550,
                            java.awt.Image.SCALE_SMOOTH);
                    javax.swing.ImageIcon scaledIcon = new javax.swing.ImageIcon(scaledImage);

                    backgroundManager.setIcon(scaledIcon);
                    isBackgroundChanged = false;
                    btnSettings.setText("Thay đổi BG");
                    System.out.println("Đã khôi phục background gốc thành công!");
                } else {
                    throw new Exception("Không thể load được background gốc");
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi khôi phục background: " + e.getMessage());
                e.printStackTrace();
                // Fallback: set icon to null nếu không thể load được
                backgroundManager.setIcon(null);
                isBackgroundChanged = false;
                btnSettings.setText("Thay đổi BG");
            }
        }
    }

    /**
     * Animation thay đổi background
     */
    private void animateBackgroundChange() {
        try {
            System.out.println("Đang thử load background: coffee-chain-manager.jpg");

            // Thử load từ resource path trước
            javax.swing.ImageIcon backgroundIcon = null;
            java.net.URL resourceUrl = getClass().getResource("/poly/cafe/images/backgrounds/coffee-chain-manager.jpg");

            if (resourceUrl != null) {
                System.out.println("Tìm thấy resource: " + resourceUrl.toString());
                backgroundIcon = new javax.swing.ImageIcon(resourceUrl);
            } else {
                System.out.println("Resource path không hoạt động, thử file path...");
                // Thử load từ file path
                String filePath = "src/main/java/poly/cafe/images/backgrounds/coffee-chain-manager.jpg";
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    System.out.println("Tìm thấy file: " + file.getAbsolutePath());
                    backgroundIcon = new javax.swing.ImageIcon(file.getAbsolutePath());
                } else {
                    System.err.println("File không tồn tại: " + file.getAbsolutePath());
                }
            }

            // Kiểm tra và scale image nếu load thành công
            if (backgroundIcon != null && backgroundIcon.getImage() != null) {
                // Scale image theo kích thước của backgroundManager
                java.awt.Image scaledImage = backgroundIcon.getImage().getScaledInstance(740, 550,
                        java.awt.Image.SCALE_SMOOTH);
                javax.swing.ImageIcon scaledIcon = new javax.swing.ImageIcon(scaledImage);

                backgroundManager.setIcon(scaledIcon);
                isBackgroundChanged = true;
                btnSettings.setText("Khôi phục BG");
                System.out.println("Đã thay đổi background thành công!");
            } else {
                System.err.println("Không thể load được file background: coffee-chain-manager.jpg");
                System.err.println("Resource URL: " + (resourceUrl != null ? resourceUrl.toString() : "null"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi thay đổi background: " + e.getMessage());
            e.printStackTrace();
        } finally {
            backgroundTimer.stop();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // /* Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        System.out.println("User logged in: " + XAuth.user);
        System.out.println("Is Manager? " + XAuth.user.isBranchManager());
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("Button.background", new Color(102, 153, 255));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));

                PolyCafeJFrame frame = new PolyCafeJFrame();
                frame.setVisible(true);

                // Đảm bảo frame hiển thị ở trên cùng
                frame.setAlwaysOnTop(true);
                frame.toFront();
                frame.requestFocus();

                // Tắt always on top sau khi đã focus
                javax.swing.Timer focusTimer = new javax.swing.Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.setAlwaysOnTop(false);
                        ((javax.swing.Timer) e.getSource()).stop();
                    }
                });
                focusTimer.setRepeats(false);
                focusTimer.start();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backgroundManager;
    private javax.swing.JButton btnBillManager;
    private javax.swing.JButton btnCardManager;
    private javax.swing.JButton btnCategoryManager;
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnDrinkManager;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnRevenueManager;
    private javax.swing.JButton btnSales;
    private javax.swing.JButton btnUserManager;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbLogoName;
    private javax.swing.JPanel pnlManager;
    private javax.swing.JLabel lblClock;
    private javax.swing.JButton btnSettings;
    private Timer clockTimer; // Timer để cập nhật đồng hồ mỗi giây
    private Timer blinkTimer; // Timer cho hiệu ứng nhấp nháy dấu ":"
    private Timer backgroundTimer; // Timer cho hiệu ứng thay đổi background
    private boolean showColon = true; // Biến để kiểm soát việc hiển thị dấu ":"
    private boolean isBackgroundChanged = false; // Biến để kiểm soát trạng thái background
    // End of variables declaration//GEN-END:variables
}
