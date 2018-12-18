package com.pyg.order.service;
import java.util.List;
import java.util.Set;

import com.pyg.pojo.TbPayLog;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface PayLogService {

	/* 
	 * @Desc: 分页查询
	 * @Date: 2018/12/15 
	 */
	public PageResult findPage(TbPayLog payLog, int pageNum, int pageSize,String createTime);
}
