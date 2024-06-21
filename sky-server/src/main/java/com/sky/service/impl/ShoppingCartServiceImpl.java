package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //从线程空间获取用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断加入的是套餐还是菜品
        if (Objects.isNull(shoppingCartDTO.getDishId())){
            //添加的是套餐
            Setmeal stmeal = setmealMapper.getStmealById(shoppingCartDTO.getSetmealId());
            shoppingCart.setAmount(stmeal.getPrice());
            shoppingCart.setName(stmeal.getName());
            shoppingCart.setNumber(1);
            shoppingCart.setImage(stmeal.getImage());
            shoppingCart.setCreateTime(LocalDateTime.now());

        }else {
            //添加的是菜品
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setName(dish.getName());
            shoppingCart.setNumber(1);
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setCreateTime(LocalDateTime.now());
        }
       ShoppingCart newShoppingCart = new ShoppingCart();
        //判断当前该用户有没有该产品，根据userId
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(BaseContext.getCurrentId());
        for (ShoppingCart cart : shoppingCarts){
            //判断是菜品还是套餐
            if (!Objects.isNull(cart.getDishId())){
                //是菜品，判断是哪一个菜品
                if (Objects.equals(cart.getDishId(), shoppingCart.getDishId())&&Objects.equals(cart.getDishFlavor(), shoppingCart.getDishFlavor())){
                    newShoppingCart = cart;
                }
            }else if (Objects.equals(cart.getSetmealId(), shoppingCart.getSetmealId())){
                newShoppingCart = cart;
            }


        }
        if (newShoppingCart.getId()==null){
            //没有查到，添加
            newShoppingCart = shoppingCart;
            shoppingCartMapper.insertShoppingCart(newShoppingCart);
        }else {
            //查到了，修改
            newShoppingCart.setNumber(newShoppingCart.getNumber()+1);
            shoppingCartMapper.update(newShoppingCart);
        }

    }

    /**
     * 返回购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(BaseContext.getCurrentId());
        return shoppingCarts;
    }

    /**
     *
     */
    @Override
    public void cleam() {
        shoppingCartMapper.deleteAll(BaseContext.getCurrentId());
    }

    @Override
    public void subOne(ShoppingCartDTO shoppingCartDTO) {
        //过滤出要修改的选型
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(BaseContext.getCurrentId());
        ShoppingCart shoppingCart = new ShoppingCart();
        for (ShoppingCart cart : shoppingCarts) {

            if (Objects.equals(shoppingCartDTO.getDishId(), cart.getDishId())&&Objects.equals(shoppingCartDTO.getDishFlavor(),cart.getDishFlavor())){
                shoppingCart = cart;
            }
            if (Objects.equals(shoppingCart.getSetmealId(),cart.getSetmealId())){
                shoppingCart = cart;
            }
        }
        if (Objects.equals(shoppingCart.getNumber(),1)){
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }else {
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartMapper.update(shoppingCart);
        }
    }
    }
