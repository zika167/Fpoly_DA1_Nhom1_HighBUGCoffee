package poly.cafe.util;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.io.File;

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
}