package com.taotao.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public abstract class BaseService<T extends BasePojo> {
    
    /**
     * 使用Spring4.x的新特性，通过泛型注入
     */
    @Autowired
    private Mapper<T> mapper;

//    public abstract Mapper<T> getMapper();

    /**
     * 根据id查询数据
     * 
     * @param id
     * @return
     */
    public T queryById(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 根据AND条件查询数据
     * 
     * @param t
     * @return
     */
    public List<T> queryByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 查询所有的数据
     * 
     * @return
     */
    public List<T> queryAll() {
        return this.queryByWhere(null);
    }

    /**
     * 分页查询数据
     * 
     * @param page
     * @param rows
     * @return
     */
    public PageInfo<T> queryPageList(Integer page, Integer rows) {
        return this.queryPageListByWhere(null, page, rows);
    }

    /**
     * 按照条件分页查询数据
     * 
     * @param t
     * @param page
     * @param rows
     * @return
     */
    public PageInfo<T> queryPageListByWhere(T t, Integer page, Integer rows) {
        // 设置分页参数
        PageHelper.startPage(page, rows, true);
        return new PageInfo<T>(this.queryByWhere(t));
    }

    /**
     * 根据条件查询一条数据
     * 
     * @param t
     * @return
     */
    public T queryOne(T t) {
        return this.mapper.selectOne(t);
    }

    /**
     * 新增数据，所有的字段
     * 
     * @param t
     * @return
     */
    public Integer save(T t) {
        return this.mapper.insert(t);
    }

    /**
     * 新增数据，不为null的字段
     * 
     * @param t
     * @return
     */
    public Integer saveSelective(T t) {
        return this.mapper.insertSelective(t);
    }

    /**
     * 更新数据，所有的字段
     * 
     * @param t
     * @return
     */
    public Integer updateById(T t) {
        return this.mapper.updateByPrimaryKey(t);
    }

    /**
     * 更新数据，不为null的字段
     * 
     * @param t
     * @return
     */
    public Integer updateSelective(T t) {
        return this.mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 根据id删除数据
     * 
     * @param id
     * @return
     */
    public Integer deleteById(Long id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据ids批量删除
     * 
     * @param ids
     * @param clazz
     * @return
     */
    public Integer deleteByIds(List<Object> ids, Class<T> clazz) {
        Example example = new Example(clazz);
        example.createCriteria().andIn("id", ids);
        return this.mapper.deleteByExample(example);
    }
}
