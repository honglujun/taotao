package com.taotao.sso.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.sso.pojo.User;
import com.taotao.sso.service.UserService;

@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检测数据是否可用
     * 
     * @param param
     * @param type
     * @return
     */
    @RequestMapping(value = "check/{param}/{type}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkUser(@PathVariable("param") String param,
            @PathVariable("type") Integer type) {
        try {
            Boolean bool = this.userService.check(param, type);
            return ResponseEntity.ok(bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * 用户注册
     * 
     * @param user
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> register(@Valid User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                // 校验未通过
                List<String> strs = new ArrayList<String>();
                List<ObjectError> allErrors = bindingResult.getAllErrors();
                for (ObjectError objectError : allErrors) {
                    strs.add(objectError.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StringUtils.join(strs, '|'));
            }
            this.userService.saveUser(user);
            // 201
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 登录
     * 
     * @param username
     * @param passwd
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = { "u", "p" }, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> login(@RequestParam("u") String username, @RequestParam("p") String passwd) {
        try {
            String token = this.userService.login(username, passwd);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 根据token查询用户信息
     * 
     * @param token
     * @return
     */
    @RequestMapping(value = "{token}", method = RequestMethod.GET)
    public ResponseEntity<User> queryByToken(@PathVariable("token") String token) {
        try {
            User user = this.userService.queryByToken(token);
            if (null == user) {
                // 不存在
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
