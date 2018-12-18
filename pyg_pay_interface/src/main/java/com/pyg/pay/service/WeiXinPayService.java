package com.pyg.pay.service;

import java.util.Map;

/*
 * @Desc: 微信支付接口
 * @Date: 2018/12/13 
 */
public interface WeiXinPayService {
    
    /* 
     * @Desc: 生成微信支付二维码
     * @Date: 2018/12/13 
     */
    public Map createNative(String out_trade_no,String total_fee);

    /* 
     * @Desc: 查询支付状态
     * @Date: 2018/12/13 
     */
    public Map queryPayStatus(String out_trade_no);
    
    /* 
     * @Desc: 关闭微信支付接口
     * @Date: 2018/12/16 
     */
    public Map closePay(String orderId);
}
