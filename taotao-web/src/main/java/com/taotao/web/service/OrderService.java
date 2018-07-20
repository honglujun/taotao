package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.Order;

@Service
public class OrderService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_ORDER_SUBMIT_ORDER}")
    private String TAOTAO_ORDER_SUBMIT_ORDER;

    @Value("${TAOTAO_ORDER_QUERY_ORDER}")
    private String TAOTAO_ORDER_QUERY_ORDER;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String submitOrder(Order order) {
        try {
            HttpResult httpResult = this.apiService.doPostJson(TAOTAO_ORDER_SUBMIT_ORDER,
                    MAPPER.writeValueAsString(order));
            // http 响应为200
            if (httpResult.getCode() == 200) {
                JsonNode jsonNode = MAPPER.readTree(httpResult.getData());
                // 成功的业务标识：200
                if (jsonNode.get("status").intValue() == 200) {
                    // 创建订单成功,返回订单号
                    return jsonNode.get("data").asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据订单id查询订单数据
     * 
     * @param orderId
     * @return
     */
    public Order queryOrderById(String orderId) {
        try {
            String url = TAOTAO_ORDER_QUERY_ORDER + "/" + orderId;
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData, Order.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
