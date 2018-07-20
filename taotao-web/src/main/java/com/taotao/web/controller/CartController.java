package com.taotao.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.Cart;
import com.taotao.web.bean.User;
import com.taotao.web.service.CartCookieService;
import com.taotao.web.service.CartLoginService;
import com.taotao.web.threadlocal.UserThreadLocal;

@RequestMapping("cart")
@Controller
public class CartController {

    @Autowired
    private CartLoginService cartLoginService;

    @Autowired
    private CartCookieService cartCookieService;

    @RequestMapping(value = "add/{itemId}", method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录
            this.cartCookieService.addItemToCart(itemId, request, response);
        } else {
            // 已登录
            this.cartLoginService.addItemToCart(itemId);
        }

        // 跳转重定向
        return "redirect:/cart/show.html";
    }

    /**
     * 查询购物车数据
     * 
     * @return
     */
    @RequestMapping(value = "show", method = RequestMethod.GET)
    public ModelAndView showCart(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("cart");
        List<Cart> cartList = null;
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录
            cartList = this.cartCookieService.queryCartList(request);
        } else {
            // 已登录
            cartList = this.cartLoginService.queryCartList();
        }

        mv.addObject("cartList", cartList);
        return mv;
    }

    /**
     * 更新商品数量
     * 
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateNum(@PathVariable("itemId") Long itemId,
            @PathVariable("num") Integer num, HttpServletRequest request, HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录
            this.cartCookieService.updteNum(itemId, num, request, response);
        } else {
            // 已登录
            this.cartLoginService.updateNum(itemId, num);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 移除商品
     * 
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "delete/{itemId}", method = RequestMethod.GET)
    public String updateNum(@PathVariable("itemId") Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录
            this.cartCookieService.delete(itemId, request, response);
        } else {
            // 已登录
            this.cartLoginService.delete(itemId);
        }
        // 跳转重定向
        return "redirect:/cart/show.html";
    }

}
