package com.taotao.web.mq.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.web.service.ItemService;

@Component
public class ItemHandler {

    @Autowired
    private RedisService redisService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandler.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void execute(String msg) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("商品处理器接收到消息，消息内容为：{}", msg);
        }

        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            
            String key = ItemService.REDIS_ITEM_KEY + jsonNode.get("itemId").asLong();
            this.redisService.del(key);
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("商品处理器接处理消息成功！！！！，消息内容为：{}", msg);
            }
        } catch (Exception e) {
            LOGGER.error("处理商品消息出错，msg = " + msg, e);
        }

    }

}
