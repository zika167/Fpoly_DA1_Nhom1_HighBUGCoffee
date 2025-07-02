package poly.cafe.util;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * Lớp tiện ích hỗ trợ làm việc với CSDL quan hệ
 *
 * @author NghiemN
 * @version 1.0
 */
public class XJdbc {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/DuAnMau_PolyCafe";
    private static final String USERNAME = "wangquockhanh";
    private static final String PASSWORD = "matkhau123";

    static {
        try {
            Class.forName(DRIVER); // Nạp driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy JDBC Driver", e);
        }
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    // UPDATE, INSERT, DELETE
    public static int executeUpdate(String sql, Object... values) {
        try (
            Connection conn = openConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, values)
        ) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SELECT → Trả về ResultSet (nếu cần dùng ngoài)
    public static ResultSet executeQuery(String sql, Object... values) {
        System.out.println("Executing SQL: " + sql + " with values: " + Arrays.toString(values));
        try {
            Connection conn = openConnection();
            System.out.println("Connection successful to: " + DB_URL);
            PreparedStatement stmt = prepareStatement(conn, sql, values);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // SELECT → Trả về 1 giá trị đơn
    public static <T> T getValue(String sql, Object... values) {
        try (
            Connection conn = openConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, values);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return (T) rs.getObject(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Chuẩn bị câu lệnh
    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... values) throws SQLException {
        PreparedStatement stmt = sql.trim().startsWith("{")
            ? conn.prepareCall(sql)
            : conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
        return stmt;
    }

    // Test thử
    public static void main(String[] args) {
    List<Map<String, Object>> list = new ArrayList<>();
    try (
        Connection conn = openConnection();
        PreparedStatement stmt = prepareStatement(conn, "SELECT * FROM Categories");
        ResultSet rs = stmt.executeQuery()
    ) {
        while (rs.next()) {
            System.out.println("Id: " + rs.getString("Id") + ", Name: " + rs.getString("Name"));
            
        }
    } catch (Exception e) {
        e.printStackTrace();
      }
}
}