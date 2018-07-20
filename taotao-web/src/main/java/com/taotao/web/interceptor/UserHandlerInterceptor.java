package com.taotao.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.User;
import com.taotao.web.controller.UserController;
import com.taotao.web.service.UserService;
import com.taotao.web.threadlocal.UserThreadLocal;
import com.taotao.web.utils.CookieUtils;

public class UserHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = CookieUtils.getCookieValue(request, UserController.COOKIE_TOKEN);
        if (StringUtils.isEmpty(token)) {
            UserThreadLocal.set(null);
            return true;
        }

        // 从sso系统中查询token是否有效
        User user = this.userService.queryUserByToken(token);
        if (null == user) {
            UserThreadLocal.set(null);
            return true;
        }
        
        //放到ThreadLocal中方便后续的线程中获取
        UserThreadLocal.set(user);
        
        //通过放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {

    }

}
