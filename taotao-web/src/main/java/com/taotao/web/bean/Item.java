package com.taotao.web.bean;

public class Item extends com.taotao.manage.pojo.Item{
    
    public String[] getImages(){
        return getImage() == null ? null : getImage().split(",");
    }

}
