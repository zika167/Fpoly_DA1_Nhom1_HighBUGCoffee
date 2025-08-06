package poly.cafe.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

public class XJdbc {

    private static String DRIVER;
    private static String DB_URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        loadDatabaseConfig();
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy JDBC Driver: " + DRIVER, e);
        }
    }

    private static void loadDatabaseConfig() {
        Properties props = new Properties();
        try (InputStream input = XJdbc.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Không tìm thấy file database.properties. Lỗi: " + e.getMessage());
        }
        DRIVER = props.getProperty("database.driver", "com.mysql.cj.jdbc.Driver");
        DB_URL = props.getProperty("database.url", "jdbc:mysql://localhost:3306/duan1_highbugcoffee");
        USERNAME = props.getProperty("database.username", "root");
        PASSWORD = props.getProperty("database.password", "");
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    
    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... args) throws SQLException {
        PreparedStatement stmt;
        if (sql.trim().startsWith("{")) {
            stmt = conn.prepareCall(sql);
        } else {
            stmt = conn.prepareStatement(sql);
        }
        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
        return stmt;
    }

    public static int executeUpdate(String sql, Object... args) {
        try (
            Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, args)
        ) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thực thi executeUpdate", e);
        }
    }

    public static ResultSet executeQuery(String sql, Object... args) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = prepareStatement(conn, sql, args);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thực thi executeQuery", e);
        }
    }
    
    public static long executeInsertAndGetGeneratedId(String sql, Object... args) {
        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            for (int i = 0; i < args.length; i++) {
                pstmt.setObject(i + 1, args[i]);
            }
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            throw new RuntimeException("Lỗi khi chèn dữ liệu và lấy ID tự tăng.", e);
        }
        return -1L;
    }
}