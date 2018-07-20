package com.taotao.search.bean;

import java.util.List;

public class PageInfo<T> {

    private Integer totalPage;

    private Integer total;

    private List<T> rows;

    public PageInfo() {

    }

    public PageInfo(Integer total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public PageInfo(Integer total, List<T> rows, Integer pageSize) {
        this.total = total;
        this.rows = rows;

        if (total % pageSize == 0) {
            this.totalPage = total / pageSize;
        } else {
            this.totalPage = (total / pageSize) + 1;
        }

    }

    public PageInfo(Integer totalPage, Integer total, List<T> rows) {
        this.totalPage = totalPage;
        this.total = total;
        this.rows = rows;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
