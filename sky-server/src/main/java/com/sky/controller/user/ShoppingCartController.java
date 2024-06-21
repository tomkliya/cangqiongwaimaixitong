package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端—购物车模块")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 享购物车添加
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result shoppingCartAdd(@RequestBody ShoppingCartDTO shoppingCartDTO) {

        log.info("向购物车添加：",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCart() {
        log.info("用户在正在查询购物车");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }
    @DeleteMapping("/clean")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.cleam();
        return Result.success();
    }
    @PostMapping("/sub")
   public Result subOneShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除",shoppingCartDTO);
        shoppingCartService.subOne(shoppingCartDTO);
        return Result.success();
    }
}
