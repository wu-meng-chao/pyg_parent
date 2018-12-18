package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
import com.pyg.pay.service.WeiXinPayService;
import com.pyg.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

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
    private OrderService orderService;
    /* 
     * @Desc: 生成二维码
     * @Date: 2018/12/13 
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        if(payLog!=null){
         return  weiXinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
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
                orderService.updateOrderStatus(out_trade_no, (String) map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if(x>=100){
                result=new Result(false,"二维码超时");
                break;
            }
        }
        return result;
    }
}
