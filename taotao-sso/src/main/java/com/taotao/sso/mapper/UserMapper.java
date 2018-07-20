package com.taotao.sso.mapper;

import org.apache.ibatis.annotations.Param;

import com.taotao.sso.pojo.User;

public interface UserMapper {

    public User queryUserByWhere(@Param("column") String column, @Param("param") String param);

    public void save(User user);

}
