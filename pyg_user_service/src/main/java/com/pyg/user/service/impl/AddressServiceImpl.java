package com.pyg.user.service.impl;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbAddressMapper;
import com.pyg.pojo.TbAddress;
import com.pyg.pojo.TbAddressExample;
import com.pyg.pojo.TbAddressExample.Criteria;
import com.pyg.user.service.AddressService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;



	//增加
	@Override
	public void add(TbAddress address) {
	    address.setCreateDate(new Date());
		addressMapper.insert(address);		
	}

	
	//修改
	@Override
	public void update(TbAddress address){
		addressMapper.updateByPrimaryKey(address);
	}	
	
	//根据ID获取实体
	@Override
	public TbAddress findOne(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	//删除
	@Override
	public void delete(Long id) {
	    addressMapper.deleteByPrimaryKey(id);
	}
	
	


	/* 
	 * @Desc: 获取收货人地址
	 * @Date: 2018/12/12 
	 */
    @Override
    public List<TbAddress> findListByUserId(String userId) {
        TbAddressExample explame=new TbAddressExample();
        Criteria criteria = explame.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<TbAddress> addressList = addressMapper.selectByExample(explame);
        return addressList;
    }

    /* 
     * @Desc: 设置默认地址
     * @Date: 2018/12/13 
     */
    @Override
    public void setDefault(String name,Long id) {
        //修改之前默认项
        TbAddressExample example=new TbAddressExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDefaultEqualTo("1");
        criteria.andUserIdEqualTo(name);
        List<TbAddress> addresses = addressMapper.selectByExample(example);
        for (TbAddress address : addresses) {
            address.setIsDefault("0");
            addressMapper.updateByPrimaryKey(address);
        }
        //设置默认
        TbAddress address = addressMapper.selectByPrimaryKey(id);
        address.setIsDefault("1");
        addressMapper.updateByPrimaryKey(address);
    }

}
