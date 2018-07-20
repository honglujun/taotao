package com.taotao.web.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.bean.TaotaoResult;
import com.taotao.web.bean.Order;
import com.taotao.web.bean.User;
import com.taotao.web.service.CartLoginService;
import com.taotao.web.service.ItemService;
import com.taotao.web.service.OrderService;
import com.taotao.web.service.UserService;
import com.taotao.web.threadlocal.UserThreadLocal;

@RequestMapping("order")
@Controller
public class OrderController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CartLoginService cartLoginService;

    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("order");
        // 模型数据：商品对象
        mv.addObject("item", this.itemService.queryItemById(itemId));
        return mv;
    }
    
    /**
     * 去购物车的结算页
     * 
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView toCartOrder() {
        ModelAndView mv = new ModelAndView("order-cart");
        // 模型数据：商品对象
        mv.addObject("carts", this.cartLoginService.queryCartList());
        return mv;
    }

    /**
     * 提交订单
     */
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult submitOrder(Order order) {
        try {
            // 查询并且设置用户信息
            // User user = this.userService.queryUserByToken(token);

            User user = UserThreadLocal.get();
            order.setUserId(user.getId());
            order.setBuyerNick(user.getUsername());

            String orderId = this.orderService.submitOrder(order);
            if (null != orderId) {
                return TaotaoResult.ok(orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TaotaoResult.errror();
    }

    @RequestMapping(value = "success", method = RequestMethod.GET)
    public ModelAndView success(@RequestParam("id") String orderId) {
        ModelAndView mv = new ModelAndView("success");

        // 订单数据
        mv.addObject("order", this.orderService.queryOrderById(orderId));
        // 当前时间加2天
        mv.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));

        return mv;
    }

}
