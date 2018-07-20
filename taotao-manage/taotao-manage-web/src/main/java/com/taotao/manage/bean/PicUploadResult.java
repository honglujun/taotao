package com.taotao.manage.bean;

public class PicUploadResult {
    
    //0:成功 1：不成功
    private Integer error;
    
    //上传成功后的图片访问地址
    private String url;
    
    // 宽
    private String width;
    
    // 高
    private String height;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
    
    

}
