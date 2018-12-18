package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.Cart;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /* 
     * @Desc: 添加商品到购物车
     * @Date: 2018/12/10 
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1、根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("商品不存在！");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品已过期！");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartsBySellerId(cartList, sellerId);
        if (cart== null) {//4.如果购物车列表中不存在该商家的购物车
            //4.1 新建该商家的购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            List<TbOrderItem> orderItemList=new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(num, item);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else {//5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem==null){//5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(num, item);
                cart.getOrderItemList().add(orderItem);
            }else {//5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0，则移除
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果移除后cart的明细数量为0，则将cart移除
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        };
        return cartList;
    }

    /* 
     * @Desc: 根据商家Id查询购物车列表是否含有该商家
     * @Date: 2018/12/10 
     */
    private Cart searchCartsBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /*
     * @Desc: 创建订单明细
     * @Date: 2018/12/10
     */
    private TbOrderItem createOrderItem(Integer num, TbItem item) {
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    /* 
     * @Desc: 根据商品Id查询购物车明细列表中是否存在该商品
     * @Date: 2018/12/10 
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /*
     * @Desc: 从redis中提取购物车列表
     * @Date: 2018/12/11
     */
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        if(cartList==null){
            return new ArrayList<>();
        }

        return cartList;
    }

    /*
     * @Desc: 将新的购物车列表存到redis中
     * @Date: 2018/12/11
     */
    @Override
    public void saveCartListToRedis(String name, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(name,cartList);
    }

    /* 
     * @Desc: 合并购物车
     * @Date: 2018/12/11 
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem:cart.getOrderItemList()){
                cartList1 = addGoodsToCartList(cartList1,orderItem.getItemId() ,orderItem.getNum() );
            }
        }
        return cartList1;
    }
}
