package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.dishWithFlavor(dishDTO);
       return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询:{}", dishPageQueryDTO);
        PageResult result =  dishService.pageQuery(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 菜品的删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品的删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除的菜品ids:{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品的数据:{}" ,dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }
    @ApiOperation("通过id查询")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询菜品的id{}",id);
        DishVO dishVO =  dishService.getById(id);
        return Result.success(dishVO);
    }

}
