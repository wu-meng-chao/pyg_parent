package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import util.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${appid}")
    private String appid;//公众账号
    @Value("${partner}")
    private String partner;//商户号
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;//回调地址

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1、创建参数
        Map<String, String> param=new HashMap();
        param.put("appid",appid);//公众账号
        param.put("mch_id",partner);//商户号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        param.put("body","品优购");//商品内容
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//标价金额（分）
        param.put("spbill_create_ip","127.0.0.1");//终端IP
        param.put("notify_url",notifyurl);//回调地址（随便写）
        param.put("trade_type","NATIVE");//交易类型
        try {
            //2、生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("要发送的参数："+xmlParam);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);//是否是https协议
            httpClient.setXmlParam(xmlParam);//发送的xml数据
            httpClient.post();//执行post请求
            //3、获取结果
            String xmlResult = httpClient.getContent();
            System.out.println("要接受的结果："+xmlResult);
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            Map map=new HashMap();
            map.put("code_url", mapResult.get("code_url"));//支付地址
            map.put("out_trade_no", out_trade_no);//订单号
            map.put("total_fee",total_fee);//总金额
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    /* 
     * @Desc: 查询支付状态
     * @Date: 2018/12/13 
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1、创建参数
        Map param=new HashMap();
        param.put("appid",appid);//公众账号
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        try {
            //2、生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3、获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("接受的结果："+xmlResult);
            return mapResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* 
     * @Desc: 关闭微信支付接口
     * @Date: 2018/12/16 
     */
    @Override
    public Map closePay(String orderId) {
        Map param=new HashMap();
        param.put("appid",appid);//公众号ID
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",orderId );//商户订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);//生成要发送的xml
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            return mapResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
