package com.taotao.web.threadlocal;

import com.taotao.web.bean.User;


public class UserThreadLocal {

    private static final ThreadLocal<User> USER = new ThreadLocal<User>();

    /**
     * 将User对象放置到ThreadLocal中
     * 
     * @param user
     */
    public static void set(User user) {
        USER.set(user);
    }

    /**
     * 从ThreadLocal中获取User对象
     * 
     * @return
     */
    public static User get() {
        return USER.get();
    }
}
