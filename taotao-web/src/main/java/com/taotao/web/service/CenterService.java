package com.taotao.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.web.threadlocal.UserThreadLocal;

@Service
public class CenterService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_ORDER_URL}")
    private String TAOTAO_ORDER_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Map<String, Object> queryOrderByOrderid(Integer page, Integer rows) {

        String url = TAOTAO_ORDER_URL + "/order/query/" + UserThreadLocal.get().getUsername() + "/" + page
                + "/" + rows;
      
        try {
            String jsondata = this.apiService.doGet(url);
            if (null == jsondata) {
                return new HashMap<String, Object>(0);
            }
            return MAPPER.readValue(jsondata, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>(0);
    }
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    public Map querymyorderOrderByOrderId(String orderId) {

        String url = TAOTAO_ORDER_URL + "/order/query/" + orderId;
        try {
            String jsondata = this.apiService.doGet(url);

            if (null == jsondata) {
                return new HashMap<String, Object>(0);
            }
            return MAPPER.readValue(jsondata, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>(0);
    }

}
