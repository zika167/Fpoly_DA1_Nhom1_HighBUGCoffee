package poly.cafe.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

/**
 * Utility class để xử lý touchpad compatibility
 * Giải quyết vấn đề khác biệt giữa mouse click và touchpad click
 */
public class TouchpadCompatibility {

    /**
     * Thêm mouse listener với touchpad compatibility
     * 
     * @param component    Component cần thêm listener
     * @param clickHandler Handler xử lý click event
     */
    public static void addTouchpadCompatibleClickListener(JComponent component, Runnable clickHandler) {
        component.addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;
            private static final long DOUBLE_CLICK_TIME = 300; // milliseconds

            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();

                // Xử lý single click
                if (currentTime - lastClickTime > DOUBLE_CLICK_TIME) {
                    // Đây là single click
                    clickHandler.run();
                }

                lastClickTime = currentTime;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Đảm bảo component có focus khi touchpad click
                if (!component.hasFocus()) {
                    component.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Thêm mouse listener với double-click detection cho touchpad
     * 
     * @param component          Component cần thêm listener
     * @param singleClickHandler Handler xử lý single click
     * @param doubleClickHandler Handler xử lý double click
     */
    public static void addTouchpadCompatibleDoubleClickListener(JComponent component,
            Runnable singleClickHandler,
            Runnable doubleClickHandler) {
        component.addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;
            private static final long DOUBLE_CLICK_TIME = 300; // milliseconds
            private boolean isDoubleClick = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastClickTime <= DOUBLE_CLICK_TIME) {
                    // Đây là double click
                    isDoubleClick = true;
                    doubleClickHandler.run();
                } else {
                    // Đây có thể là single click, nhưng cần đợi một chút để xem có double click
                    // không
                    isDoubleClick = false;
                    new Thread(() -> {
                        try {
                            Thread.sleep(DOUBLE_CLICK_TIME);
                            if (!isDoubleClick) {
                                // Không có double click, thực hiện single click
                                singleClickHandler.run();
                            }
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }

                lastClickTime = currentTime;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Đảm bảo component có focus khi touchpad click
                if (!component.hasFocus()) {
                    component.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Kiểm tra xem có phải touchpad click không
     * 
     * @param e MouseEvent
     * @return true nếu có thể là touchpad click
     */
    public static boolean isTouchpadClick(MouseEvent e) {
        // Touchpad thường có các đặc điểm khác với mouse
        // Đây là heuristic đơn giản dựa trên thời gian click
        return e.getClickCount() == 1 && e.getModifiersEx() == 0;
    }

    /**
     * Điều chỉnh sensitivity cho touchpad
     * 
     * @param component Component cần điều chỉnh
     */
    public static void adjustTouchpadSensitivity(JComponent component) {
        // Tăng kích thước hit area cho touchpad
        component.setFocusable(true);
        component.setRequestFocusEnabled(true);

        // Thêm padding cho touchpad dễ click hơn
        component.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }
}
