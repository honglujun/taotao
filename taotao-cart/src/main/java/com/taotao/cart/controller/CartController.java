package com.taotao.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.cart.pojo.Cart;
import com.taotao.cart.service.CartService;

@RequestMapping("cart")
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 商品加入购物车
     * 
     * TODO：对Cart输入参数做校验
     * 
     * @param cart
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> saveItemToCart(Cart cart) {
        try {
            this.cartService.saveItemToCart(cart);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 根据用户id查询购物车数据
     * 
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<Cart>> queryCartListByUserId(@PathVariable("userId") Long userId) {
        try {
            List<Cart> carts = this.cartService.queryCartListByUserId(userId);
            return ResponseEntity.status(HttpStatus.OK).body(carts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 更新购买数量
     * 
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "{userId}/{itemId}/{num}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateNum(@PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        try {
            this.cartService.updateNum(userId, itemId, num);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 将商品移除购物车
     * 
     * @param userId
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{userId}/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId) {
        try {
            this.cartService.delete(userId, itemId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
