package poly.cafe.util;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

/**
 * Utility class để load icon coffee.png với nhiều fallback options
 * Giải quyết vấn đề icon không hiển thị trên các máy khác nhau
 */
public class IconUtils {
    
    /**
     * Load icon coffee.png với nhiều fallback options
     * @param targetSize Kích thước icon mong muốn (width = height)
     * @return ImageIcon đã được scale, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon(int targetSize) {
        return loadCoffeeIcon(targetSize, targetSize);
    }
    
    /**
     * Load icon coffee.png với nhiều fallback options
     * @param width Chiều rộng mong muốn
     * @param height Chiều cao mong muốn
     * @return ImageIcon đã được scale, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon(int width, int height) {
        ImageIcon icon = null;
        
        // Thứ tự ưu tiên các đường dẫn
        String[] resourcePaths = {
            "/poly/cafe/images/icons/coffee.png",
            "/poly/cafe/images/logo/coffee.png", 
            "/poly/cafe/images/logo/logo.png",
            "/poly/cafe/images/logo/logo.jpg"
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
                "src/main/java/poly/cafe/images/logo/coffee.png",
                "src/main/java/poly/cafe/images/logo/logo.png", 
                "src/main/java/poly/cafe/images/logo/logo.jpg"
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
     * @return ImageIcon 24x24, hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadCoffeeIcon() {
        return loadCoffeeIcon(24, 24);
    }
}