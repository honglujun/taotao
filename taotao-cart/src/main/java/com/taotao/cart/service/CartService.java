package com.taotao.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.taotao.cart.mapper.CartMapper;
import com.taotao.cart.pojo.Cart;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    public void saveItemToCart(Cart cart) {
        // 判断该商品是否已经存在购物车中
        Cart param = new Cart();
        param.setUserId(cart.getUserId());
        param.setItemId(cart.getItemId());
        Cart oldCart = this.cartMapper.selectOne(param);
        if (null == oldCart) {
            // 该商品不存在，新增操作
            cart.setId(null);
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            this.cartMapper.insert(cart);
        } else {
            // 该商品存在，修改购买数量
            oldCart.setNum(oldCart.getNum() + cart.getNum());
            oldCart.setUpdated(new Date());
            this.cartMapper.updateByPrimaryKey(oldCart);
        }
    }

    /**
     * 按照更新时间倒序排序
     * 
     * @param userId
     * @return
     */
    public List<Cart> queryCartListByUserId(Long userId) {
        Example example = new Example(Cart.class);
        example.createCriteria().andEqualTo("userId", userId);
        example.setOrderByClause("updated DESC");
        return this.cartMapper.selectByExample(example);
    }

    /**
     * 
     * @param userId
     * @param itemId
     * @param num 全量购买数量
     * @return
     */
    public void updateNum(Long userId, Long itemId, Integer num) {
        // 构造更新条件
        Example example = new Example(Cart.class);
        example.createCriteria().andEqualTo("userId", userId).andEqualTo("itemId", itemId);

        // 更新数据
        Cart cart = new Cart();
        cart.setNum(num);

        // 执行更新
        this.cartMapper.updateByExampleSelective(cart, example);
    }

    public void delete(Long userId, Long itemId) {
        // 构造删除条件
        Example example = new Example(Cart.class);
        example.createCriteria().andEqualTo("userId", userId).andEqualTo("itemId", itemId);

        this.cartMapper.deleteByExample(example);
    }

}
