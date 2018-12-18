package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询全部品牌
    public List<TbBrand> findAll();
    //分页
    public PageResult findPage(int pageNum,int rows);
    //增加品牌
    public void addBrand(TbBrand tbBrand);
    //回显
    public TbBrand findOne(Long id);
    //修改
    public void update(TbBrand tbBrand);
    //批量删除
    public void delete(Long[] ids);
    //条件查询
    public PageResult search(TbBrand tbBrand,int page,int rows);
    //品牌下拉框数据
    public List<Map> selectOptionList();
}
