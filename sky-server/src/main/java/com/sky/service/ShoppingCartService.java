package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加
     * @param shoppingCartDTO
     */
   void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空
     */
    void cleam();

    void subOne(ShoppingCartDTO shoppingCartDTO);

}
