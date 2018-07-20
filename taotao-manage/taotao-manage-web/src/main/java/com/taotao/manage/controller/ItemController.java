package com.taotao.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemDescService;
import com.taotao.manage.service.ItemService;

@RequestMapping("item")
@Controller
public class ItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemDescService itemDescService;

    /**
     * 根据商品id查询商品数据
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ResponseEntity<Item> queryItemById(@PathVariable("itemId") Long itemId) {
        try {
            Item item = this.itemService.queryById(itemId);
            if(null == item){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 新增商品数据
     * 
     * @param item
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> saveItem(Item item, @RequestParam("desc") String desc,
            @RequestParam("itemParams") String itemParams) {
        try {
            if (LOGGER.isDebugEnabled()) {// 判断是否开启debug
                LOGGER.debug("新增商品! title = {}, cid = {}, price = {}", item.getTitle(), item.getCid(),
                        item.getPrice());
            }

            this.itemService.saveItem(item, desc, itemParams);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增商品成功 ! title = {}, cid = {}, price = {}", item.getTitle(), item.getCid(),
                        item.getPrice());
            }

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            LOGGER.error("新增商品失败！ title = " + item.getTitle(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 更新商品数据
     * 
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> updateItem(Item item, @RequestParam("desc") String desc,
            @RequestParam("itemParamId") Long itemParamId, @RequestParam("itemParams") String itemParams) {
        try {
            if (LOGGER.isDebugEnabled()) {// 判断是否开启debug
                LOGGER.debug("更新商品! title = {}, cid = {}, price = {}", item.getTitle(), item.getCid(),
                        item.getPrice());
            }

            this.itemService.updateItem(item, desc, itemParamId, itemParams);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("更新商品成功 ! title = {}, cid = {}, price = {}", item.getTitle(), item.getCid(),
                        item.getPrice());
            }
            // 204
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            LOGGER.error("更新商品失败！ title = " + item.getTitle(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询商品列表
     * 
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EasyUIResult> queryItemList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows) {
        try {
            PageInfo<Item> pageInfo = this.itemService.queryPageList(page, rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            LOGGER.error("查询增商品失败！ page = " + page + ", rows = " + rows, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
