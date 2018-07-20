package com.taotao.web.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.User;

@Service
public class UserService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_SSO_LOGIN_REGISTER}")
    private String TAOTAO_SSO_LOGIN_REGISTER;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Boolean doRegister(User user) {
        try {
            Map<String, Object> params = new HashMap<String, Object>(3);
            params.put("username", user.getUsername());
            params.put("password", user.getPassword());
            params.put("phone", user.getPhone());
            HttpResult httpResult = this.apiService.doPost(TAOTAO_SSO_LOGIN_REGISTER, params);
            return httpResult.getCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 登录
     * 
     * @param user
     * @return
     */
    public String doLogin(User user) {
        try {
            Map<String, Object> params = new HashMap<String, Object>(2);
            params.put("u", user.getUsername());
            params.put("p", user.getPassword());
            HttpResult httpResult = this.apiService.doPost(TAOTAO_SSO_LOGIN_REGISTER, params);
            if (httpResult.getCode() == 200 && StringUtils.isNotEmpty(httpResult.getData())) {
                return httpResult.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据token查询用户数据
     * 
     * @param token
     * @return
     */
    public User queryUserByToken(String token) {
        try {
            String url = TAOTAO_SSO_LOGIN_REGISTER + "/" + token;
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData, User.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
