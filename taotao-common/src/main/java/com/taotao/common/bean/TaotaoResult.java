package com.taotao.common.bean;

public class TaotaoResult {

    private Integer status;

    private String msg;

    private Object data;

    public TaotaoResult() {

    }

    public TaotaoResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static TaotaoResult ok() {
        return ok(null);
    }

    public static TaotaoResult ok(Object data) {
        return new TaotaoResult(200, "ok", data);
    }

    public static TaotaoResult errror() {
        return new TaotaoResult(500, "error", null);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
