package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

   void insertShoppingCart(ShoppingCart shoppingCart);


   List<ShoppingCart> getByUserId(Long userId);

   void update(ShoppingCart newShoppingCart);

   @Delete("delete from sky_take_out.shopping_cart where user_id = #{userId}")
   void deleteAll(Long userId);

   @Delete("delete from sky_take_out.shopping_cart where id = #{id}")
   void deleteById(Long id);
}

