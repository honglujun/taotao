package com.taotao.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.Cart;
import com.taotao.web.bean.Item;
import com.taotao.web.threadlocal.UserThreadLocal;

@Service
public class CartLoginService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private ItemService itemService;

    @Value("${TAOTAO_CART_URL}")
    private String TAOTAO_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Boolean addItemToCart(Long itemId) {
        String url = TAOTAO_CART_URL + "/rest/cart";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", UserThreadLocal.get().getId());
        params.put("itemId", itemId);

        Item item = this.itemService.queryItemById(itemId);

        params.put("itemTitle", item.getTitle());
        params.put("itemImage", item.getImages()[0]);
        params.put("itemPrice", item.getPrice());
        params.put("num", 1);// TODO：默认实现1，项目实战中完成页面中数量功能

        try {
            HttpResult httpResult = this.apiService.doPost(url, params);
            if (httpResult.getCode() == 201) {
                // 添加成功
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据用户id查询购物车数据
     * 
     * @return
     */
    public List<Cart> queryCartList() {
        try {
            String url = TAOTAO_CART_URL + "/rest/cart/" + UserThreadLocal.get().getId();
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新购买商品数量
     * 
     * @param itemId
     * @param num
     * @return
     */
    public Boolean updateNum(Long itemId, Integer num) {
        String url = TAOTAO_CART_URL + "/rest/cart/" + UserThreadLocal.get().getId() + "/" + itemId + "/"
                + num;
        try {
            HttpResult httpResult = this.apiService.doPut(url);
            if (httpResult.getCode() == 204) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除购物车中的商品
     * 
     * @param itemId
     * @return
     */
    public Boolean delete(Long itemId) {
        String url = TAOTAO_CART_URL + "/rest/cart/" + UserThreadLocal.get().getId() + "/" + itemId;
        try {
            HttpResult httpResult = this.apiService.doDelete(url);
            if (httpResult.getCode() == 204) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
