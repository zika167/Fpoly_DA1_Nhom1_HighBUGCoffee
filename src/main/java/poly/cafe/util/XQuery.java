package poly.cafe.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.entity.Category;
import poly.cafe.entity.Drink;
import poly.cafe.entity.User;

/**
 * Lớp tiện ích hỗ trợ truy vấn và chuyển đổi sang đối tượng
 *
 * @author NghiemN
 * @version 1.0
 */
public class XQuery {
   
    /**
     * Truy vấn 1 đối tượng
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param beanClass lớp của đối tượng kết quả
     * @param sql câu lệnh truy vấn
     * @param values các giá trị cung cấp cho các tham số của SQL
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
    public static <B> B getSingleBean(Class<B> beanClass, String sql, Object... values) {
        List<B> list = XQuery.getBeanList(beanClass, sql, values);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Truy vấn nhiều đối tượng
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param beanClass lớp của đối tượng kết quả
     * @param sql câu lệnh truy vấn
     * @param values các giá trị cung cấp cho các tham số của SQL
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
    public static <B> List<B> getBeanList(Class<B> beanClass, String sql, Object... values) {
    List<B> list = new ArrayList<>();
    try {
        ResultSet resultSet = XJdbc.executeQuery(sql, values);
        System.out.println("ResultSet opened, starting loop...");
        while (resultSet.next()) {
            System.out.println("Processing row in ResultSet...");
            list.add(XQuery.readBean(resultSet, beanClass));
        }
        System.out.println("Loop completed, list size: " + list.size());
    } catch (Exception ex) {
        System.out.println("Error in getBeanList: " + ex.getMessage());
        ex.printStackTrace();
        throw new RuntimeException(ex);
    }
    return list;
}

    /**
     * Tạo bean với dữ liệu đọc từ bản ghi hiện tại
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param resultSet tập bản ghi cung cấp dữ liệu
     * @param beanClass lớp của đối tượng kết quả
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
//    private static <B> B readBean(ResultSet resultSet, Class<B> beanClass) throws Exception {
//        B bean = beanClass.getDeclaredConstructor().newInstance();
//        Method[] methods = beanClass.getDeclaredMethods();
//        for(Method method: methods){
//            String name = method.getName();
//            if (name.startsWith("set") && method.getParameterCount() == 1) {
//                try {
//                    Object value = resultSet.getObject(name.substring(3));
//                    method.invoke(bean, value);
//                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
//                    System.out.printf("+ Column '%s' not found!\r\n", name.substring(3));
//                }
//            }
//        }
//        return bean;
//    }
    
    public static <B> B readBean(ResultSet resultSet, Class<B> beanClass) throws Exception {
        B bean = beanClass.getDeclaredConstructor().newInstance();
        Method[] methods = beanClass.getDeclaredMethods();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            System.out.println("Column " + i + ": " + metaData.getColumnName(i) + " (Type: " + metaData.getColumnTypeName(i) + ")");
        }

        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && method.getParameterCount() == 1) {
                String propertyName = name.substring(3);
                String columnName = null;

                for (int i = 1; i <= columnCount; i++) {
                    if (metaData.getColumnName(i).equalsIgnoreCase(propertyName)) {
                        columnName = metaData.getColumnName(i);
                        break;
                    }
                }

                if (columnName == null) {
                    System.out.printf("+ Column '%s' not found!\n", propertyName);
                    continue;
                }

                try {
                    Object value = resultSet.getObject(columnName);
                    if (value != null) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        System.out.println("Mapping " + propertyName + " (Type: " + paramType.getSimpleName() + ") with value: " + value);
                        if (paramType == boolean.class || paramType == Boolean.class) {
                            value = resultSet.getBoolean(columnName);
                        } else if (paramType == double.class || paramType == Double.class) {
                            value = resultSet.getDouble(columnName);
                        } else if (paramType == int.class || paramType == Integer.class) {
                            value = resultSet.getInt(columnName);
                        }
                        method.invoke(bean, value);
                    }
                } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.out.printf("+ Error mapping column '%s': %s\n", columnName, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
    
    public static void main(String[] args) {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        User user = XQuery.getSingleBean(User.class, sql, "user1");
        System.out.println("User: " + user);
    }
}