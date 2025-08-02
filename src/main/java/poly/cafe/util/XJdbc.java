package poly.cafe.util;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Lớp tiện ích hỗ trợ làm việc với CSDL quan hệ
 *
 * @author NghiemN
 * @version 1.0
 */
public class XJdbc {
    private static Connection connection;

    private static String DRIVER;
    private static String DB_URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        loadDatabaseConfig();
        try {
            Class.forName(DRIVER); // Nạp driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy JDBC Driver", e);
        }
    }

    /**
     * Load cấu hình database từ file properties hoặc biến môi trường
     */
    private static void loadDatabaseConfig() {
        Properties props = new Properties();
        
        // Thử đọc từ file cấu hình trước
        try (InputStream input = XJdbc.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("Đã load cấu hình từ file database.properties");
            }
        } catch (IOException e) {
            System.out.println("Không tìm thấy file database.properties, sử dụng cấu hình mặc định");
        }

        // Đọc từ file cấu hình hoặc sử dụng giá trị mặc định
        DRIVER = props.getProperty("database.driver", "com.mysql.cj.jdbc.Driver");
        DB_URL = props.getProperty("database.url", "jdbc:mysql://localhost:3306/duan1_highbugcoffee");
        USERNAME = props.getProperty("database.username", "root");
        PASSWORD = props.getProperty("database.password", "");

        // Ưu tiên biến môi trường nếu có
        String envUrl = System.getenv("DB_URL");
        String envUsername = System.getenv("DB_USERNAME");
        String envPassword = System.getenv("DB_PASSWORD");

        if (envUrl != null) DB_URL = envUrl;
        if (envUsername != null) USERNAME = envUsername;
        if (envPassword != null) PASSWORD = envPassword;

        System.out.println("Database URL: " + DB_URL);
        System.out.println("Database Username: " + USERNAME);
    }

    public static Connection openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        }
        return connection;
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