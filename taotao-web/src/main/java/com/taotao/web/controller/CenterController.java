package com.taotao.web.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.Order;
import com.taotao.web.service.CenterService;
import com.taotao.web.service.OrderService;

@RequestMapping("center")
@Controller
public class CenterController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CenterService centerService;

    @RequestMapping(value = "my/orders", method = RequestMethod.GET)
    public ModelAndView myOrders(@RequestParam( value = "page" ,defaultValue ="1") Integer page,
            @RequestParam( value ="rows",defaultValue ="5") Integer rows) {
        ModelAndView mv = new ModelAndView("my-orders");
        
        //添加模型数据 返回值 要看拿到的是啥数据 具体分析
        Map<String,Object> map = this.centerService.queryOrderByOrderid(page,rows);
        mv.addObject("orders",map);
        mv.addObject("page",page);
        //计算总页
        Integer totle =Integer.valueOf(map.get("totle").toString());
        mv.addObject("totalpage",(totle + rows-1)/rows);
        

        return mv;
    }
   
//   /**
//    * 根据订单ID查询订单
//    * @param orderId
//    * @return
//    */
//   @ResponseBody
//   @RequestMapping(value = "/query/{orderId}" ,method = RequestMethod.GET)
//   public Order queryOrderById(@PathVariable("orderId") String orderId) {
//           return orderService.queryOrderById(orderId);
//   }
    
   @RequestMapping(value = "my/{orderId}", method = RequestMethod.GET)
    public ModelAndView myOrder(@PathVariable("orderId") String orderId) {
       ModelAndView mv = new ModelAndView("orderMore");
       
       mv.addObject("orderMore",this.centerService.querymyorderOrderByOrderId(orderId));
       
       return mv;
    }
}
