package poly.cafe.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XQuery {

    public static <B> B getSingleBean(Class<B> beanClass, String sql, Object... values) {
        List<B> list = getBeanList(beanClass, sql, values);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public static <B> List<B> getBeanList(Class<B> beanClass, String sql, Object... values) {
        List<B> list = new ArrayList<>();
        try (ResultSet resultSet = XJdbc.executeQuery(sql, values)) {
            Map<String, Method> setters = new HashMap<>();
            for (Method method : beanClass.getMethods()) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    setters.put(method.getName().substring(3).toUpperCase(), method);
                }
            }
            while (resultSet.next()) {
                list.add(readBean(resultSet, beanClass, setters));
            }
        } catch (Exception ex) {
            System.err.println("Lỗi trong getBeanList: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return list;
    }

    private static <B> B readBean(ResultSet resultSet, Class<B> beanClass, Map<String, Method> setters) throws Exception {
        B bean = beanClass.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i).toUpperCase();
            Method setter = setters.get(columnLabel);

            if (setter != null) {
                try {
                    Object value = resultSet.getObject(i);
                    if (value != null) {
                        value = convertToSetterType(value, setter.getParameterTypes()[0]);
                        setter.invoke(bean, value);
                    }
                } catch (Exception e) {
                    System.err.printf("+ Lỗi khi map cột '%s': %s\n", metaData.getColumnLabel(i), e.getMessage());
                }
            }
        }
        return bean;
    }

    private static Object convertToSetterType(Object value, Class<?> setterType) {
        if (setterType == Date.class && value instanceof java.time.LocalDateTime) {
            java.time.LocalDateTime ldt = (java.time.LocalDateTime) value;
            java.time.ZonedDateTime zdt = ldt.atZone(java.time.ZoneId.systemDefault());
            return java.util.Date.from(zdt.toInstant());
        }
        if (setterType == Date.class && value instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) value).getTime());
        }
        if ((setterType == int.class || setterType == Integer.class) && !(value instanceof Integer)) {
             return ((Number) value).intValue();
        }
        if ((setterType == long.class || setterType == Long.class) && !(value instanceof Long)) {
            return ((Number) value).longValue();
        }
        if ((setterType == double.class || setterType == Double.class) && !(value instanceof Double)) {
            return ((Number) value).doubleValue();
        }
        if (setterType == BigDecimal.class && !(value instanceof BigDecimal)) {
            return new BigDecimal(value.toString());
        }
        if (setterType.isEnum() && value instanceof String) {
            return Enum.valueOf((Class<Enum>) setterType, (String) value);
        }
        return value;
    }
}