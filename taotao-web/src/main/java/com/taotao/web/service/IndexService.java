package com.taotao.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.Content;

@Service
public class IndexService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;

    @Value("${INDEX_AD1_URL}")
    private String INDEX_AD1_URL;

    @Value("${INDEX_AD2_URL}")
    private String INDEX_AD2_URL;
    
    @Autowired
    private RedisService redisService;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static final String REDIS_INDEX1_KEY = "TAOTAO_WEB_INDEX_AD1";
    
    public static final Integer REDIS_INDEX1_TIME = 60 * 60 * 24;

    public String queryIndexAd1() {
        
        try {
            //先从缓存缓存中命中
            String cacheData = this.redisService.get(REDIS_INDEX1_KEY);
            if(StringUtils.isNotEmpty(cacheData)){
                //命中，返回
                return cacheData;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        String url = TAOTAO_MANAGE_URL + INDEX_AD1_URL;
        try {
            String jsonData = this.apiService.doGet(url);

            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            // 解析json
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            ArrayNode rows = (ArrayNode) jsonNode.get("rows");
            for (JsonNode row : rows) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("srcB", row.get("pic").asText());
                map.put("height", 240);
                map.put("alt", row.get("title").asText());
                map.put("width", 670);
                map.put("src", map.get("srcB"));
                map.put("widthB", 550);
                map.put("href", row.get("url").asText());
                map.put("heightB", 240);
                result.add(map);
            }
            
            if(result.isEmpty()){
                //如果集合为空，说明没有查询到数据，直接返回
                return null;
            }
            
            String resultJson = MAPPER.writeValueAsString(result);
            
            try {
                //将结果集写入到redis中
                this.redisService.set(REDIS_INDEX1_KEY, resultJson, REDIS_INDEX1_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return resultJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Object queryIndexAd2() {
        String url = TAOTAO_MANAGE_URL + INDEX_AD2_URL;
        try {
            String easyUIResultJsonData = this.apiService.doGet(url);
            EasyUIResult easyUIResult = EasyUIResult.formatToList(easyUIResultJsonData, Content.class);
            List<Content> contents = (List<Content>) easyUIResult.getRows();
            Map<String, Object> result = new HashMap<String, Object>();
            for (Content content : contents) {
                result.put("width", 310);
                result.put("height", 70);
                result.put("src", content.getPic());
                result.put("href", content.getUrl());
                result.put("alt", content.getTitle());
                result.put("widthB", 210);
                result.put("heightB", 70);
                result.put("srcB", content.getPic());
            }
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
