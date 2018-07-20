package com.taotao.sso.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.sso.mapper.UserMapper;
import com.taotao.sso.pojo.User;

@Service
public class UserService {

    private static final Map<Integer, String> TYPES = new HashMap<Integer, String>();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    private static final String MD5_KEY = "77ebe594bd50dd32b127bb62a4d19f56";

    private static final String TOKEN = "TOKEN_";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        TYPES.put(1, "username");
        TYPES.put(2, "phone");
        TYPES.put(3, "email");
    }

    public Boolean check(String param, Integer type) throws Exception {
        // 验证type是否合法
        if (!TYPES.containsKey(type)) {
            // 非法
            throw new Exception("请求参数非法！ type只能是1、2、3");
        }
        User user = this.userMapper.queryUserByWhere(TYPES.get(type), param);

        // True：数据可用，false：数据不可用
        return null == user;
    }

    public void saveUser(User user) {
        // 对密码加密
        user.setPassword(DigestUtils.md5Hex(MD5_KEY + user.getPassword()));
        this.userMapper.save(user);
    }

    public String login(String username, String passwd) throws Exception {
        User user = this.userMapper.queryUserByWhere("username", username);
        if (null == user) {
            return null;
        }
        // 比对密码是否一致
        if (StringUtils.equals(user.getPassword(), DigestUtils.md5Hex(MD5_KEY + passwd))) {
            // 密码正确，登录成功
            // 生成token
            String token = DigestUtils.md5Hex(username + "_" + System.currentTimeMillis());

            // 把用户数据写入到redis中
            this.redisService.set(TOKEN + token, MAPPER.writeValueAsString(user), 3600);

            // 登录成功
            return token;
        }
        // 密码不正确，登录失败
        return null;
    }

    public User queryByToken(String token) throws Exception {
        String key = TOKEN + token;
        String data = this.redisService.get(key);
        if (StringUtils.isEmpty(data)) {
            return null;
        }

        // 刷新数据时间
        this.redisService.expire(key, 3600);

        return MAPPER.readValue(data, User.class);
    }

}
