package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService.CartService;
import com.pyg.pojo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /*
     * @Desc: 提取购物车列表
     * @Date: 2018/12/10
     */
    @RequestMapping("/findCartList")
    private List<Cart> findCartList() {
        //获取登录名
        String loginName = getLoginName();
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartListStr == null || cartListStr == "") {
            cartListStr = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr, Cart.class);

        if(loginName.equals("anonymousUser")){//如果登录名为anonymousUser，则从Cookie中提取购物车列表
            return cartList_cookie;
        }else {//此时表示已登录，从redis中获取购物车列表
            List<Cart> cartList_redis =cartService.findCartListFromRedis(loginName);
            if(cartList_cookie.size()>0){
                cartList_redis = cartService.mergeCartList(cartList_cookie,cartList_redis );
                CookieUtil.deleteCookie(request,response ,"cartList");
                cartService.saveCartListToRedis(loginName,cartList_redis );
            }
            return cartList_redis;
        }
    }

    /* 
     * @Desc: 添加商品
     * @Date: 2018/12/10 
     */
    //@CrossOrigin(origins = "http://localhost:9105",allowCredentials="true")
    @RequestMapping("/addGoodsToCart")
    public Result addGoodsToCart(Long itemId,Integer num){
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            //获取登录名
            String name = getLoginName();
            if(name.equals("anonymousUser")){//添加商品到缓存
                //1、从Cookie中提取购物车
                List<Cart> cartList = findCartList();
                //2、调用服务操作购物车
                cartList=cartService.addGoodsToCartList(cartList,itemId ,num );
                //3、将新的购物车存入Cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
            }else {//添加商品到redis
                List<Cart> cartList = findCartList();//提取
                cartList = cartService.addGoodsToCartList(cartList,itemId,num);//操作
                cartService.saveCartListToRedis(name,cartList);//保存
            }

            return new Result(true,"添加购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败！");
        }

    }

    /* 
     * @Desc: 获取登录名
     * @Date: 2018/12/11 
     */
    private String getLoginName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }
}
