package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.EmptyFileException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j

public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void dishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
       //复制属性到Dish中
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //获取插入菜品的ID
        Long id = dish.getId();
        //插入味道
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
        dishFlavorMapper.insertBatch(flavors);
    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        if (!Objects.isNull(dishPageQueryDTO.getCategoryId())||
        !Objects.isNull(dishPageQueryDTO.getName())||
        !Objects.isNull(dishPageQueryDTO.getStatus())) {
            dishPageQueryDTO.setPage(dishPageQueryDTO.getPage()-1);
        }
        log.info("DTo{}", dishPageQueryDTO);
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 删除菜品
     * @param ids
     */

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //判断是否在售卖
        for (Long id : ids) {
           Dish dish =  dishMapper.getById(id);
           if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
               throw new DeletionNotAllowedException("存在起售中的菜品");
           }

        }
        //判断有没有关联的套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIds(ids);
        if (!setmealIds.isEmpty()){
            throw new DeletionNotAllowedException("有套餐存在当前菜品，无法删除");
        }
        //删除菜品
        dishMapper.delete(ids);
        log.info("菜品批量删除：{}",ids);
        //删除对应的菜品口味
        dishFlavorMapper.delete(ids);
    }

    /**
     * 跟新菜品数据和口味
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //口味数据更新
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        //删除旧数据
        dishFlavorMapper.delete(ids);
        //插入新数据
        dishDTO.getFlavors().forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
        if (dishDTO.getFlavors().isEmpty()){
            throw new EmptyFileException(MessageConstant.CANNOT_EMPTY);
        }else {
            dishFlavorMapper.insertBatch(dishDTO.getFlavors());
        }

    }

    /**
     * 根据id查询返回菜品和口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dish.getId());
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
