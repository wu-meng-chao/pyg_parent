package com.pyg.user.service;
import java.util.List;
import com.pyg.pojo.TbAddress;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	// 增加
	public void add(TbAddress address);
	
	
	//修改
	public void update(TbAddress address);
	

	//根据ID获取实体
	public TbAddress findOne(Long id);
	
	
	//删除
	public void delete(Long id);
	
	/* 
	 * @Desc: 获取收货人地址
	 * @Date: 2018/12/12 
	 */
	public List<TbAddress> findListByUserId(String userId);

	/* 
	 * @Desc: 设置默认地址
	 * @Date: 2018/12/13 
	 */
    public void setDefault(String name,Long id);
}
