package com.taotao.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.web.bean.Cart;
import com.taotao.web.bean.Item;
import com.taotao.web.utils.CookieUtils;

@Service
public class CartCookieService {

    public static final String TT_CART = "TT_CART";

    private static final Integer COOKIE_TIME = 60 * 60 * 24 * 30 * 3;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemService itemService;

    public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = this.queryCartList(request);

        Cart cart = null;

        for (Cart c : carts) {
            if (c.getItemId().intValue() == itemId.intValue()) {
                cart = c;
                break;
            }
        }

        if (cart == null) {
            // 该商品不存在，直接加入
            cart = new Cart();
            Item item = this.itemService.queryItemById(itemId);
            cart.setItemId(itemId);
            cart.setItemImage(item.getImages()[0]);
            cart.setItemPrice(item.getPrice());
            cart.setItemTitle(item.getTitle());
            cart.setCreated(new Date());
            cart.setNum(1);// TODO
            cart.setUpdated(cart.getCreated());

            carts.add(cart);
        } else {
            cart.setNum(cart.getNum() + 1);// TODO 默认1
        }

        // 购物车数据写入到cookie
        try {
            CookieUtils.setCookie(request, response, TT_CART, MAPPER.writeValueAsString(carts), COOKIE_TIME,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从cookie中查询购物车数据
     * 
     * @param request
     * @return
     */
    public List<Cart> queryCartList(HttpServletRequest request) {
        String jsonData = CookieUtils.getCookieValue(request, TT_CART, true);
        if (StringUtils.isNotEmpty(jsonData)) {
            try {
                return MAPPER.readValue(jsonData,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<Cart>(0);
    }

    public Boolean updteNum(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        List<Cart> carts = this.queryCartList(request);

        Cart cart = null;

        for (Cart c : carts) {
            if (c.getItemId().intValue() == itemId.intValue()) {
                cart = c;
                break;
            }
        }

        if (null != cart) {
            // 设置数量
            cart.setNum(num);

            // 购物车数据写入到cookie
            try {
                CookieUtils.setCookie(request, response, TT_CART, MAPPER.writeValueAsString(carts),
                        COOKIE_TIME, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    public Boolean delete(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = this.queryCartList(request);

        for (Cart c : carts) {
            if (c.getItemId().intValue() == itemId.intValue()) {
                carts.remove(c);

                // 购物车数据写入到cookie
                try {
                    CookieUtils.setCookie(request, response, TT_CART, MAPPER.writeValueAsString(carts),
                            COOKIE_TIME, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        }
        return false;
    }

}
