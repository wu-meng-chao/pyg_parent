package com.pyg.order.service;
import java.util.List;
import com.pyg.pojo.TbOrder;

import com.pyg.pojo.TbPayLog;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);

	
	/* 
	 * @Desc:根据用户ID查询日志
	 * @Date: 2018/12/14 
	 */
	public TbPayLog searchPayLogFromRedis(String userId);
	
	/* 
	 * @Desc: 修改订单状态
	 * @Date: 2018/12/14 
	 */
	public void updateOrderStatus(String out_trade_no,String transaction_id);
}
