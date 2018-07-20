package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.service.IndexService;

@RequestMapping("index")
@Controller
public class IndexController {
    
    @Autowired
    private IndexService indexService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        //查询大广告位数据
        String jsonData = this.indexService.queryIndexAd1();
        mv.addObject("indexAd1", jsonData);
        //查询右上角广告
        mv.addObject("indexAd2", this.indexService.queryIndexAd2());
        return mv;
    }

}
