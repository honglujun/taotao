package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;
import com.taotao.web.bean.Item;

@Service
public class ItemService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private RedisService redisService;

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;

    public static final String REDIS_ITEM_KEY = "TAOTAO_WEB_ITEM_INFO_";
    
    public static final Integer REDIS_ITEM_TIME = 60 * 60 * 24 * 30;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Item queryItemById(Long itemId) {
        String key = REDIS_ITEM_KEY + itemId;
        try {
            String jsonData = this.redisService.get(key);
            if(StringUtils.isNotEmpty(jsonData)){
                return MAPPER.readValue(jsonData, Item.class);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String url = TAOTAO_MANAGE_URL + "/rest/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (jsonData == null) {
                // 没有查询到数据
                return null;
            }
            
            try {
                //将结果接写入到redis中
                this.redisService.set(key, jsonData, REDIS_ITEM_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return MAPPER.readValue(jsonData, Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemDesc queryItemDescByItemId(Long itemId) {
        String url = TAOTAO_MANAGE_URL + "/rest/item/desc/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (jsonData == null) {
                // 没有查询到数据
                return null;
            }
            return MAPPER.readValue(jsonData, ItemDesc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String queryItemPatramItemByItemId(Long itemId) {
        String url = TAOTAO_MANAGE_URL + "/rest/item/param/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (jsonData == null) {
                // 没有查询到数据
                return null;
            }

            ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
            String paramJson = itemParamItem.getParamData();

            // 解析json
            JsonNode jsonNode = MAPPER.readTree(paramJson);
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            StringBuilder sb = new StringBuilder();
            sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\"><tbody>");
            for (JsonNode node : arrayNode) {
                String group = node.get("group").asText();
                sb.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + group + "</th></tr>");
                ArrayNode params = (ArrayNode) node.get("params");
                for (JsonNode param : params) {
                    sb.append("<tr><td class=\"tdTitle\">" + param.get("k").asText() + "</td><td>"
                            + param.get("v").asText() + "</td></tr>");
                }
            }
            sb.append("</tbody></table>");
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
