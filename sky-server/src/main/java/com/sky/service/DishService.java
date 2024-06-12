package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和味道
     * @param dishDTO
     */
    void dishWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改菜品数据
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getById(Long id);
}
