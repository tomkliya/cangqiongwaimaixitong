package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "用户相关接口")
@Slf4j
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);


        Map<String,Object> map = new HashMap<>();

        map.put(JwtClaimsConstant.USER_ID,user.getId());
        String userJwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), map);

        UserLoginVO vo = UserLoginVO.builder().id(user.getId())
                .token(userJwt)
                .openid(user.getOpenid())
                .build();

        return Result.success(vo);
    }
}
