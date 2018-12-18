package com.pyg.manage.controller;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbPayLog;
import com.pyg.order.service.PayLogService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/payLog")
public class PayLogController {

	@Reference
	private PayLogService payLogService;

	/*
	 * @Desc: 分页查询
	 * @Date: 2018/12/15 
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbPayLog payLog, int page, int rows ,String createTime ){
        System.out.println(createTime);
        return payLogService.findPage(payLog, page, rows,createTime);
	}

}
