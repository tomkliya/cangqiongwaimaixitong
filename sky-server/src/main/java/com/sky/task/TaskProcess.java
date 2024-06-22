package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TaskProcess {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 0/1 1 ? * ?")
    public void ProcessTimeOutOrder(){
        log.info("定时处理超时订单");

        //查找待付款且超时
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(15);
        List<Orders> orders = userMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, localDateTime);

        //修改订单状态
        if (orders!=null&& !orders.isEmpty()){
            for (Orders order : orders) {
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                orderMapper.update(order);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理一直在派送的订单");

        //查找待付款且超时
        List<Orders> orders = userMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now());

        //修改订单状态
        if (orders!=null&& !orders.isEmpty()){
            for (Orders order : orders) {

                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }

    }
}
