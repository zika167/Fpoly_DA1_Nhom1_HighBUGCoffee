/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.Card;

/**
 *
 * @author wangquockhanh
 */
public interface CardDAO extends CrudDAO<Card, Integer>{
    List<Card> findByIdRange(int minId, int maxId);
}

