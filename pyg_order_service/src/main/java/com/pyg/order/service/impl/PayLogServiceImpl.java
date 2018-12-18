package com.pyg.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojo.TbPayLogExample;
import com.pyg.pojo.TbPayLogExample.Criteria;
import com.pyg.order.service.PayLogService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class PayLogServiceImpl implements PayLogService {

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/* 
	 * @Desc: 分页查询
	 * @Date: 2018/12/15 
	 */
		@Override
	public PageResult findPage(TbPayLog payLog, int pageNum, int pageSize,String createTime) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbPayLogExample example=new TbPayLogExample();
		Criteria criteria = example.createCriteria();
		
		if(payLog!=null){			
			if(payLog.getOutTradeNo()!=null && payLog.getOutTradeNo().length()>0){
				criteria.andOutTradeNoLike("%"+payLog.getOutTradeNo()+"%");
			}
			if(payLog.getUserId()!=null && payLog.getUserId().length()>0){
				criteria.andUserIdLike("%"+payLog.getUserId()+"%");
			}
			if(payLog.getTransactionId()!=null && payLog.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+payLog.getTransactionId()+"%");
			}
			if(payLog.getTradeState()!=null && payLog.getTradeState().length()>0){
				criteria.andTradeStateLike("%"+payLog.getTradeState()+"%");
			}
			if(payLog.getOrderList()!=null && payLog.getOrderList().length()>0){
				criteria.andOrderListLike("%"+payLog.getOrderList()+"%");
			}
			if(payLog.getPayType()!=null && payLog.getPayType().length()>0){
                criteria.andPayTypeLike("%"+payLog.getPayType()+"%");
            }
		}
            if(createTime!=null&&createTime!=""){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = dateFormat.parse(createTime);
                    criteria.andCreateTimeGreaterThan(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
		
		Page<TbPayLog> page= (Page<TbPayLog>)payLogMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

}
