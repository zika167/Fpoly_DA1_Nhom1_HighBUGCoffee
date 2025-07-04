/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name; // giữ nguyên logic toString như bản gốc
    }
}

//public class Category {
//    private String id;
//    private String name;
//    
//    public Category() {}
//    public Category(String id, String name) {
//        this.id = id;
//        this.name = name;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//    
//    @Override
//    public String toString() {
//        return name; // Hoặc return getName() nếu tiện hơn
//    }
//    
//    public static class Builder {
//        private final Category category;
//
//        public Builder() {
//            category = new Category();
//        }
//
//        public Builder id(String id) {
//            category.setId(id);
//            return this;
//        }
//
//        public Builder name(String name) {
//            category.setName(name);
//            return this;
//        }
//
//        public Category build() {
//            return category;
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//}

