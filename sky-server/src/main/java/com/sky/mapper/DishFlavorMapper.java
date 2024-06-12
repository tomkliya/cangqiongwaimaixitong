package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除菜品id删除
     * @param DishIds
     */
    void delete(List<Long> DishIds);

    /**
     * 根据菜品id修改味道
     * @param  dishFlavor
     */
    void update(DishFlavor dishFlavor);

    @Select("select  * from sky_take_out.dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);

    @Select("select * from sky_take_out.dish_flavor where id = #{id}")
    DishFlavor getById(Long id);
}

