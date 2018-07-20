package com.taotao.search.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.search.service.SearchService;

@Controller
public class ItemSearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam("keyWords") String keyWords,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        ModelAndView mv = new ModelAndView("search");
        
        try {
            keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        mv.addObject("query", keyWords);
        mv.addObject("page", page);
        try {
            // 搜索数据
            mv.addObject("pageInfo", this.searchService.search(keyWords, page));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }

        return mv;
    }

}
