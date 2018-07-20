package com.taotao.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.abel533.mapper.Mapper;
import com.taotao.manage.pojo.Content;

public interface ContentMapper extends Mapper<Content> {

    List<Content> queryContentListByCategoryId(@Param("categoryId") Long categoryId);

}
