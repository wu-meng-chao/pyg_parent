package com.pyg.page.service.impl;


import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired//商品表
    private TbGoodsMapper goodsMapper;
    @Autowired//商品扩展表
    private TbGoodsDescMapper goodsDescMapper;
    @Value("${pagedir}")//商品详情页生成地址
    private String pagedir;
    @Autowired//商品分类表
    private TbItemCatMapper itemCatMapper;
    @Autowired//SKU列表
    private TbItemMapper itemMapper;


    /*
     * @Desc: 生成商品详细页
     * @Date: 2018/12/3
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        configuration.setDefaultEncoding("utf-8");

        //创建数据模型
        Map dataModel=new HashMap();
        //1、加载商品表数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goods",tbGoods );
        //2、加载商品扩展表
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goodsDesc",tbGoodsDesc );
        //3、加载商品分类
        String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
        dataModel.put("itemCat1",itemCat1);
        dataModel.put("itemCat2",itemCat2);
        dataModel.put("itemCat3",itemCat3);
        //4、加载SKU列表
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andGoodsIdEqualTo(goodsId);//SPU ID相同
        example.setOrderByClause("is_default desc");//按照状态降序排序，保证第一个为默认
        List<TbItem> itemList = itemMapper.selectByExample(example);
        dataModel.put("itemList",itemList);

        try {
            //Writer out1=new FileWriter(pagedir+goodsId+".html");
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(pagedir+goodsId+".html"), "UTF-8"));
            Template template = configuration.getTemplate("item.ftl");
            template.setEncoding("utf-8");
            template.process(dataModel,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        return false;
    }
}
