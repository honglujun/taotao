package com.taotao.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.web.bean.User;
import com.taotao.web.service.UserService;
import com.taotao.web.utils.CookieUtils;

@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public static final String COOKIE_TOKEN = "TAOTAO_TOKEN";

    // 注册页面
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    // 登录页面
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * 注册功能
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "doRegister", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> doRegister(User user) {
        Map<String, Object> result = new HashMap<String, Object>(1);
        try {
            Boolean bool = this.userService.doRegister(user);
            if (bool) {
                // 注册成功
                result.put("status", "200");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("status", "500");
        return result;
    }

    /**
     * 登录功能
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>(1);
        try {
            String token = this.userService.doLogin(user);
            if (StringUtils.isNotEmpty(token)) {
                // 登录成功
                result.put("status", "200");

                // 将token写入到cookie中，该cookie的生成时间为session级别，关闭浏览器cookie失效
                CookieUtils.setCookie(request, response, COOKIE_TOKEN, token);

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("status", "500");
        return result;
    }

}
