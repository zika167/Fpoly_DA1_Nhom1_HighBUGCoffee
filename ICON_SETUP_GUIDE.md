# Hướng dẫn Setup Icon Coffee.png cho các Developer

## Vấn đề
Icon `coffee.png` không hiển thị trên các máy laptop khác do:
1. File icon không được đóng gói vào JAR file
2. Code đang sử dụng đường dẫn resource nhưng file không tồn tại trong JAR

## Giải pháp đã áp dụng

### 1. Cập nhật pom.xml
Đã thêm Maven Resources Plugin để copy thư mục images vào JAR:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.3.1</version>
    <executions>
        <execution>
            <id>copy-images</id>
            <phase>process-resources</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.outputDirectory}/poly/cafe/images</outputDirectory>
                <resources>
                    <resource>
                        <directory>src/main/java/poly/cafe/images</directory>
                        <filtering>false</filtering>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 2. Tạo IconUtils utility class
Tạo class `poly.cafe.util.IconUtils` với nhiều fallback options:

```java
// Sử dụng trong code:
ImageIcon icon = poly.cafe.util.IconUtils.loadCoffeeIcon(24, 24);
```

### 3. Cập nhật các file UI
Đã cập nhật các file sau để sử dụng IconUtils:
- `HomepageBranchManagerJFrame.java`
- `BranchRevenueManagerJDialog.java` 
- `EmployeeBranchManagerJDialog.java`

## Cách build và test

### Bước 1: Build project
```bash
# Nếu có Maven installed
mvn clean package

# Hoặc sử dụng Maven wrapper (nếu có)
./mvnw clean package
```

### Bước 2: Kiểm tra JAR file
```bash
# Kiểm tra xem images có trong JAR không
jar -tf target/Fpoly_DA1_Nhom1_HighBUGCoffee-1.0-SNAPSHOT.jar | grep images
```

### Bước 3: Test ứng dụng
```bash
java -jar target/Fpoly_DA1_Nhom1_HighBUGCoffee-1.0-SNAPSHOT.jar
```

## Fallback Strategy
IconUtils sẽ thử load icon theo thứ tự:

1. **Classpath resources** (ưu tiên cao nhất):
   - `/poly/cafe/images/icons/coffee.png`
   - `/poly/cafe/images/logo/coffee.png`
   - `/poly/cafe/images/logo/logo.png`
   - `/poly/cafe/images/logo/logo.jpg`

2. **File system** (fallback):
   - `src/main/java/poly/cafe/images/icons/coffee.png`
   - `src/main/java/poly/cafe/images/logo/coffee.png`
   - `src/main/java/poly/cafe/images/logo/logo.png`
   - `src/main/java/poly/cafe/images/logo/logo.jpg`

## Troubleshooting

### Nếu icon vẫn không hiển thị:
1. Kiểm tra file `coffee.png` có tồn tại trong `src/main/java/poly/cafe/images/icons/`
2. Build lại project với `mvn clean package`
3. Kiểm tra JAR file có chứa thư mục images không
4. Chạy ứng dụng và xem console log để debug

### Log messages:
- `"Loaded coffee icon from resource: ..."` - Load thành công từ resource
- `"Loaded coffee icon from file: ..."` - Load thành công từ file system
- `"Could not load coffee icon from any source"` - Không tìm thấy icon

## Lưu ý cho Developer mới
- Luôn sử dụng `IconUtils.loadCoffeeIcon()` thay vì load trực tiếp
- Đảm bảo file icon tồn tại trong thư mục `src/main/java/poly/cafe/images/icons/`
- Build project sau khi thêm/sửa file icon
