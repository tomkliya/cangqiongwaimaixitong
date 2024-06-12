package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminStatusController")
@Slf4j
@RequestMapping
@Api(tags = "店铺状态接口")
public class StatusController {
    @Autowired
    private RedisTemplate redisTemplate;
    static final String key = "STATUS_SHOP";

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @ApiOperation("设置")
    @PutMapping("/admin/shop/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("将营业状态设置为{}",status==1?"营业":"打烊");
        redisTemplate.opsForValue().set(key,status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @ApiOperation("获取营业状态")
    @GetMapping("/admin/shop/status")
    public Result getStatus(){
        log.info("获取营业状态");
        return Result.success(redisTemplate.opsForValue().get(key));
    }
}
