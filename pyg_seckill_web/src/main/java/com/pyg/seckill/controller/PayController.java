package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pyg.pay.service.WeiXinPayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*
 * @Desc: 支付控制层
 * @Date: 2018/12/13 
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 5000)
    private WeiXinPayService weiXinPayService;

    @Reference(timeout = 5000)
    private SeckillOrderService seckillOrderService;


    /* 
     * @Desc: 生成二维码
     * @Date: 2018/12/13 
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);
        if(seckillOrder!=null){//如果秒杀订单存在
            long fen =(long)(seckillOrder.getMoney().doubleValue() * 100);
            return  weiXinPayService.createNative(seckillOrder.getId()+"",fen+"");
        }else {
            return new HashMap();
        }
    }

    /* 
     * @Desc: 查询支付状态
     * @Date: 2018/12/13 
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result=null;
        int x=1;
        while (true){
            Map map = weiXinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                result=new Result(false,"支付发生错误！");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){
                result=new Result(true,"支付成功");
                //修改订单状态
                seckillOrderService.updateOrderStatus(userId, Long.valueOf(out_trade_no),(String) map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if(x>=20){//设置超时时间为5分钟
                result=new Result(false,"二维码超时");
                //调用微信的关闭订单接口
                Map map1 = weiXinPayService.closePay(out_trade_no);
                if(map1.get("return_code").equals("SUCCESS")){//如果返回的状态码是SUCCESS
                    if(map1.get("err_code").equals("ORDERPAID")){
                        result=new Result(true, "支付成功");
                        seckillOrderService.updateOrderStatus(userId,Long.valueOf(out_trade_no),(String) map.get("transaction_id"));
                    }
                }
                if(result.isSuccess()==false){
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }
}
