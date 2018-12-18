package com.pyg.cart.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbAddress;
import com.pyg.user.service.AddressService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;

	/**
	 * 增加收货地址
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress address){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        address.setUserId(name);
        address.setIsDefault("0");
        try {
			addressService.add(address);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbAddress findOne(Long id){
		return addressService.findOne(id);		
	}
	
	/**
	 * 删除联系人
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long id){
		try {
			addressService.delete(id);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/* 
	 * @Desc: 获取收货人地址
	 * @Date: 2018/12/12 
	 */
    @RequestMapping("/findListByLoginUser")
	public List<TbAddress> findListByLoginUser(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbAddress> addressList = addressService.findListByUserId(userName);
        return addressList;
    }
    
    /* 
     * @Desc: 设置默认
     * @Date: 2018/12/13 
     */
    @RequestMapping("/setDefault")
    public Result setDefault(Long id){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            addressService.setDefault(name,id);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
	
}
