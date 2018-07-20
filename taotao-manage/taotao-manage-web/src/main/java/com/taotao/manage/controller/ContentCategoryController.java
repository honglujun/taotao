package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.manage.pojo.ContentCategory;
import com.taotao.manage.service.ContentCategoryService;

@RequestMapping("content/category")
@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 查询商品类目列表
     * 
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ContentCategory>> queryItemCatList(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            ContentCategory category = new ContentCategory();
            category.setParentId(parentId);
            List<ContentCategory> contentCategories = this.contentCategoryService.queryByWhere(category);
            return ResponseEntity.ok(contentCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 新增分类
     * 
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ContentCategory> saveContentCategory(ContentCategory contentCategory) {
        try {
            contentCategory.setCreated(new Date());
            contentCategory.setUpdated(contentCategory.getCreated());
            contentCategory.setIsParent(false);
            contentCategory.setSortOrder(1);
            contentCategory.setStatus(1);
            // 保存分类数据到数据库
            this.contentCategoryService.saveSelective(contentCategory);

            // 判断该节点的父节点的isParent是否为true
            ContentCategory parent = this.contentCategoryService.queryById(contentCategory.getParentId());
            if (!parent.getIsParent()) {
                // 如果为false，修改isParent为true
                parent.setIsParent(true);
                parent.setUpdated(new Date());
                this.contentCategoryService.updateSelective(parent);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 重命名
     * 
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> updateContentCategory(ContentCategory contentCategory) {
        try {
            contentCategory.setUpdated(new Date());
            // 更新分类数据到数据库
            this.contentCategoryService.updateSelective(contentCategory);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 删除
     * 
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContentCategory(ContentCategory contentCategory) {
        try {
            // 递归查找所有的子节点
            // 集合：用于搜集所有待删除的节点id
            List<Object> ids = new ArrayList<Object>();
            ids.add(contentCategory.getId());
            // 查找所有子节点
            findAllSubCateory(ids, contentCategory.getId());
            this.contentCategoryService.deleteByIds(ids, ContentCategory.class);

            // 查询该节点的父节点下是否存在其他子节点，如果不存在，修改isParent为false
            ContentCategory param = new ContentCategory();
            param.setParentId(contentCategory.getParentId());
            List<ContentCategory> contentCategories = this.contentCategoryService.queryByWhere(param);
            if (contentCategories == null || contentCategories.isEmpty()) {
                // 没有其他子节点
                ContentCategory parent = new ContentCategory();
                parent.setId(contentCategory.getParentId());
                parent.setIsParent(false);
                parent.setUpdated(new Date());
                this.contentCategoryService.updateSelective(parent);
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    // 递归查询所有的子节点
    private void findAllSubCateory(List<Object> ids, Long parentId) {
        ContentCategory category = new ContentCategory();
        category.setParentId(parentId);
        List<ContentCategory> contentCategories = this.contentCategoryService.queryByWhere(category);
        for (ContentCategory contentCategory : contentCategories) {
            ids.add(contentCategory.getId());
            if (contentCategory.getIsParent()) {
                // 该节点是父节点
                findAllSubCateory(ids, contentCategory.getId());
            }
        }
    }

}
