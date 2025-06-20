package poly.cafe.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class XIcon {
    /**
     * Đọc icon từ resource hoặc file
     * @param path đường dẫn file, đường dẫn resource hoặc tên resource
     * @return ImageIcon
     */
    public static ImageIcon getIcon(String path) {
        if (path.startsWith("file:") || path.startsWith("http")) {
            try {
                return new ImageIcon(new java.net.URL(path));
            } catch (MalformedURLException ex) {
                Logger.getLogger(XIcon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Nếu là tên file đơn giản (không có dấu / hay \) thì tìm trong resource icons
        if (!path.contains("/") && !path.contains("\\")) {
            return XIcon.getIcon("/poly/cafe/icons/" + path);
        }

        // Nếu là đường dẫn nội bộ trong resource (bắt đầu bằng /)
        if (path.startsWith("/")) {
            return new ImageIcon(XIcon.class.getResource(path));
        }
        return new ImageIcon(path);
    }
    
    /**
     * Đọc icon theo kích thước
     * @param path đường dẫn file hoặc tài nguyên
     * @param width chiều rộng
     * @param height chiều cao
     * @return Icon
     */
    public static ImageIcon getIcon(String path, int width, int height) {
        Image image = getIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
    /**
     * Thay đổi icon của JLabel
     * @param label JLabel cần thay đổi
     * @param path đường dẫn file hoặc tài nguyên
     */
    public static void setIcon(JLabel label, String path) {
        label.setIcon(XIcon.getIcon(path, label.getWidth(), label.getHeight()));
    }
    /**
     * Thay đổi icon của JLabel
     * @param label JLabel cần thay đổi
     * @param file file icon
     */
    public static void setIcon(JLabel label, File file) {
        XIcon.setIcon(label, file.getAbsolutePath());
    }
    /**
     * Sao chép file vào thư mục với tên file mới là duy nhất
     * @param fromFile file cần sao chép
     * @param folder thư mục đích
     * @return File đã sao chép
     */
    public static File copyTo(File fromFile, String folder) {
        String fileExt = fromFile.getName().substring(fromFile.getName().lastIndexOf("."));
        File toFile = new File(folder, XStr.getKey() + fileExt);
        toFile.getParentFile().mkdirs();
        try {
            Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return toFile;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static File copyTo(File fromFile) {
        return copyTo(fromFile, "files");
    }
}