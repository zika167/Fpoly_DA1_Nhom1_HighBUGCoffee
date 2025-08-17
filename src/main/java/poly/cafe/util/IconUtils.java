package poly.cafe.util;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Utility class để xử lý icon một cách an toàn
 */
public class IconUtils {

    /**
     * Set icon cho button một cách an toàn, không làm crash app nếu icon không tồn
     * tại
     * 
     * @param button   Button cần set icon
     * @param iconPath Đường dẫn icon
     * @param width    Chiều rộng icon
     * @param height   Chiều cao icon
     */
    public static void setButtonIconSafe(JButton button, String iconPath, int width, int height) {
        try {
            // Thử load icon từ resource trước
            ImageIcon icon = XIcon.getIcon(iconPath, width, height);
            if (icon != null && icon.getImage() != null) {
                button.setIcon(icon);
                return;
            }
        } catch (Exception e) {
            // Nếu không load được từ resource, thử load từ file
            try {
                File iconFile = new File(
                        "src/main/java/poly/cafe/images/icons/" + iconPath.substring(iconPath.lastIndexOf("/") + 1));
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                    if (icon.getImage() != null) {
                        button.setIcon(new ImageIcon(
                                icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)));
                        return;
                    }
                }
            } catch (Exception ex) {
                // Bỏ qua lỗi, không làm gì
            }
        }

        // Nếu không load được icon, chỉ log ra console, không làm crash app
        System.out.println("Không thể load icon: " + iconPath + " cho button: " + button.getText());
    }

    /**
     * Set icon cho button một cách an toàn với kích thước mặc định 16x16
     * 
     * @param button   Button cần set icon
     * @param iconPath Đường dẫn icon
     */
    public static void setButtonIconSafe(JButton button, String iconPath) {
        setButtonIconSafe(button, iconPath, 16, 16);
    }

    /**
     * Load icon coffee.png với nhiều fallback options
     * 
     * @param targetSize Kích thước icon mong muốn (width = height)
     * @return ImageIcon đã được scale, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon(int targetSize) {
        return loadCoffeeIcon(targetSize, targetSize);
    }

    /**
     * Load icon coffee.png với nhiều fallback options
     * 
     * @param width  Chiều rộng mong muốn
     * @param height Chiều cao mong muốn
     * @return ImageIcon đã được scale, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon(int width, int height) {
        ImageIcon icon = null;

        // Thứ tự ưu tiên các đường dẫn
        String[] resourcePaths = {
                "/poly/cafe/images/icons/coffee.png",
                "/poly/cafe/images/logo/coffee.png"
        };

        // 1. Thử load từ classpath resources (ưu tiên cao nhất)
        for (String path : resourcePaths) {
            try {
                java.net.URL url = IconUtils.class.getResource(path);
                if (url != null) {
                    icon = new ImageIcon(url);
                    System.out.println("Loaded coffee icon from resource: " + path);
                    break;
                }
            } catch (Exception e) {
                System.err.println("Failed to load from resource: " + path + " - " + e.getMessage());
            }
        }

        // 2. Fallback: thử load từ file system
        if (icon == null) {
            String[] filePaths = {
                    "src/main/java/poly/cafe/images/icons/coffee.png",
                    "src/main/java/poly/cafe/images/logo/coffee.png"
            };

            for (String path : filePaths) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        icon = new ImageIcon(file.getAbsolutePath());
                        System.out.println("Loaded coffee icon from file: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load from file: " + path + " - " + e.getMessage());
                }
            }
        }

        // 3. Scale icon nếu tìm thấy
        if (icon != null && icon.getImage() != null) {
            try {
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } catch (Exception e) {
                System.err.println("Failed to scale icon: " + e.getMessage());
                return icon; // Trả về icon gốc nếu scale thất bại
            }
        }

        System.err.println("Could not load coffee icon from any source");
        return null;
    }

    /**
     * Load icon coffee.png với kích thước mặc định 24x24
     * 
     * @return ImageIcon 24x24, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon() {
        return loadCoffeeIcon(24, 24);
    }

    /**
     * Load logo icon với nhiều fallback options
     * 
     * @param width  Chiều rộng mong muốn
     * @param height Chiều cao mong muốn
     * @return ImageIcon đã được scale, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadLogoIcon(int width, int height) {
        ImageIcon icon = null;

        // Thứ tự ưu tiên các đường dẫn logo
        String[] resourcePaths = {
                "/poly/cafe/images/logo/logo.png",
                "/poly/cafe/images/logo/logo.jpg"
        };

        // 1. Thử load từ classpath resources (ưu tiên cao nhất)
        for (String path : resourcePaths) {
            try {
                java.net.URL url = IconUtils.class.getResource(path);
                if (url != null) {
                    icon = new ImageIcon(url);
                    System.out.println("Loaded logo icon from resource: " + path);
                    break;
                }
            } catch (Exception e) {
                System.err.println("Failed to load logo from resource: " + path + " - " + e.getMessage());
            }
        }

        // 2. Fallback: thử load từ file system
        if (icon == null) {
            String[] filePaths = {
                    "src/main/java/poly/cafe/images/logo/logo.png",
                    "src/main/java/poly/cafe/images/logo/logo.jpg"
            };

            for (String path : filePaths) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        icon = new ImageIcon(file.getAbsolutePath());
                        System.out.println("Loaded logo icon from file: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load logo from file: " + path + " - " + e.getMessage());
                }
            }
        }

        // 3. Scale icon nếu tìm thấy
        if (icon != null && icon.getImage() != null) {
            try {
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } catch (Exception e) {
                System.err.println("Failed to scale logo icon: " + e.getMessage());
                return icon; // Trả về icon gốc nếu scale thất bại
            }
        }

        System.err.println("Could not load logo icon from any source");
        return null;
    }

    /**
     * Load logo icon với kích thước mặc định
     * 
     * @return ImageIcon, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadLogoIcon() {
        return loadLogoIcon(440, 480); // Kích thước mặc định cho logo
    }
}