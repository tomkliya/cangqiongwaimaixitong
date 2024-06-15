package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public static final String WXUrl = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {

        String openId = getOpenid(userLoginDTO.getCode());

        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.selectUserByOpenId(openId);
        if (user == null) {
            //检查是不是新用户：是
            User newuser = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(newuser);
            return newuser;
        }else {
            //不是
            return user;
        }
    }
    private String getOpenid(String code){
        Map<String, String> params = new HashMap();
        params.put("grant_type", "authorization_code");
        params.put("appid",weChatProperties.getAppid() );
        params.put("secret",weChatProperties.getSecret());
        params.put("js_code",code);
        String result = HttpClientUtil.doGet(WXUrl, params);
        log.info("传回的参数：{}",result);
        //解析传回的参数
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getString("openid");
    }
}
