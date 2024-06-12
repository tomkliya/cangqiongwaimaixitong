package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userStatusController")
@Slf4j
@RequestMapping
@Api(tags = "营业状态设置")
public class StatusController {
    @Autowired
    private RedisTemplate redisTemplate;
    static final String key = "STATUS_SHOP";
    /**
     * 获取营业状态
     * @return
     */
    @ApiOperation("获取营业状态")
    @GetMapping("/user/shop/status")
    public Result getStatus(){
        log.info("获取营业状态");
        return Result.success(redisTemplate.opsForValue().get(key));
    }
}
