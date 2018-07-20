package com.taotao.manage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemService extends BaseService<Item> {

    @Autowired
    private ItemDescService itemDescService;

    @Autowired
    private ItemParamItemService itemParamItemService;

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_WEB_URL}")
    private String TAOTAO_WEB_URL;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Autowired//消息模板
    private RabbitTemplate rabbitTemplate;

    public void saveItem(Item item, String desc, String itemParams) {
        // 保存商品数据
        item.setId(null);// 安全
        item.setCreated(new Date());
        item.setUpdated(item.getCreated());
        item.setStatus(1);
        super.save(item);

        // 保存商品描述数据
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(itemDesc.getCreated());
        this.itemDescService.save(itemDesc);

        // 保存规格参数数据
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setItemId(item.getId());
        itemParamItem.setParamData(itemParams);
        itemParamItem.setCreated(new Date());
        itemParamItem.setUpdated(itemParamItem.getCreated());
        this.itemParamItemService.save(itemParamItem);
        
        sendMsg(item.getId(), "insert");
        
    }

    public void updateItem(Item item, String desc, Long itemParamId, String itemParams) {
        // 更新数据
        item.setUpdated(new Date());
        item.setCreated(null);// 强制不更新
        item.setStatus(null);// 强制不更新
        super.updateSelective(item);

        // 更新描述数据
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        itemDesc.setUpdated(new Date());
        this.itemDescService.updateSelective(itemDesc);

        // 更新规格参数数据
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setId(itemParamId);
        itemParamItem.setParamData(itemParams);
        itemParamItem.setUpdated(new Date());
        this.itemParamItemService.updateSelective(itemParamItem);
        
        sendMsg(item.getId(), "update");

//        try {
//            // 调用其他系统的接口实现数据的同步
//            String tataoWebUrl = TAOTAO_WEB_URL + "/item/cache/" + item.getId() + ".html";
//            this.apiService.doGet(tataoWebUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    
    private void sendMsg(Long id, String type){
        try {
            Map<String, Object> msg = new HashMap<String, Object>();
            msg.put("itemId", id);
            msg.put("type", type);
            msg.put("date", System.currentTimeMillis());
            //发送消息到RabbitMQ
            this.rabbitTemplate.convertAndSend("item."+type, MAPPER.writeValueAsString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
