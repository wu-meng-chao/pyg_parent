package com.pyg.cart.service.CartService;

import com.pyg.pojo.Cart;

import java.util.List;

public interface CartService {

    /* 
     * @Desc: 添加商品到购物车
     * @Date: 2018/12/10 
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /* 
     * @Desc: 从redis中获取购物车列表
     * @Date: 2018/12/11 
     */
    public List<Cart> findCartListFromRedis(String name);

    /* 
     * @Desc: 将购物车列表重新保存到redis中
     * @Date: 2018/12/11 
     */
    public void saveCartListToRedis(String name, List<Cart> cartList);
    
    /* 
     * @Desc: 合并购物车
     * @Date: 2018/12/11 
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
