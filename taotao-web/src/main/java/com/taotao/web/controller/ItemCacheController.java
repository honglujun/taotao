package com.taotao.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.service.RedisService;
import com.taotao.web.service.ItemService;

@RequestMapping("item/cache")
@Controller
public class ItemCacheController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCacheController.class);

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ResponseEntity<Void> deleteItemCache(@PathVariable("itemId") Long itemId) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("更新商品的缓存数据！！  itemId = {}", itemId);
            }
            String key = ItemService.REDIS_ITEM_KEY + itemId;
            this.redisService.del(key);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("更新商品的缓存数据成功！！  itemId = {}", itemId);
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
