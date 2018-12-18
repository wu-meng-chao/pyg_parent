package com.pyg.seckill.service;
import java.util.List;
import com.pyg.pojo.TbSeckillGoods;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);

	/* 
	 * @Desc:查询正在参与秒杀的商品
	 * @Date: 2018/12/15 
	 */
    public List<TbSeckillGoods> findList();

    /* 
     * @Desc: 从缓存中查询商品实体
     * @Date: 2018/12/15 
     */
    public TbSeckillGoods findOneFromRedis(Long id);
	
}
