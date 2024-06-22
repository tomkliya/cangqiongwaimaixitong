package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 查找user
     * @param openid
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User selectUserByOpenId(String openid);

    /**
     * 插入新用户
     * @param newuser
     */


    void insert(User newuser);

    @Select("select * from sky_take_out.user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据状态和下单时间查找
     * @param pendingPayment
     * @param now
     * @return
     */
    @Select("select * from sky_take_out.orders where status = #{pendingPayment} and order_time < #{now}")
    List<Orders> getByStatusAndOrderTime(Integer pendingPayment, LocalDateTime now);
}
