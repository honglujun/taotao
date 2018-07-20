package com.taotao.search.mq.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.search.bean.Item;

@Component
public class ItemHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandler.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private HttpSolrServer httpSolrServer;

    @Autowired
    private ApiService apiService;

    public void execute(String msg) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("商品处理器接收到消息，消息内容为：{}", msg);
        }

        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            String type = jsonNode.get("type").asText();
            Long itemId = jsonNode.get("itemId").asLong();
            if (StringUtils.equals(type, "delete")) {
                // 删除
                this.httpSolrServer.deleteById(String.valueOf(itemId));
            } else {
                // 新增或修改
                String url = "http://manage.taotao.com/rest/item/" + itemId;
                String jsonData = this.apiService.doGet(url);
                if (StringUtils.isEmpty(jsonData)) {
                    LOGGER.error("查询商品数据出错或id错误！ url = {}", url);
                    return;
                }
                Item item = MAPPER.readValue(jsonData, Item.class);
                //新增或更新索引数据
                this.httpSolrServer.addBean(item);
            }

            // 提交
            this.httpSolrServer.commit();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("商品处理器接处理消息成功！！！！，消息内容为：{}", msg);
            }
        } catch (Exception e) {
            LOGGER.error("处理商品消息出错，msg = " + msg, e);
        }

    }

}