package com.pyg.task;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTast {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;




    /* 
     * @Desc: 增量更新缓存
     * @Date: 2018/12/16 
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行定时任务："+new Date());
        List ids= new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        TbSeckillGoodsExample example=new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//剩余库存大于0
        criteria.andStartTimeLessThan(new Date());//开始时间小于当前时间
        criteria.andEndTimeGreaterThan(new Date());//结束时间小于当前时间
        if(ids.size()>0){
            criteria.andIdNotIn(ids);//排除缓存中已经有的商品
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
            System.out.println("增量更新秒杀商品ID："+seckillGoods.getId());
        }
    }
    
    /* 
     * @Desc: 过期秒杀商品的移除
     * @Date: 2018/12/16 
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("移除过期秒杀商品任务执行！");
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckillGood : seckillGoods) {
            if(seckillGood.getEndTime().getTime()<new Date().getTime()){//如果结束日期小于当前日期
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);//更新数据库
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());//清除缓存
                System.out.println("被移除的商品Id："+seckillGood.getId());
            }
        }
        System.out.println("移除过期秒杀商品任务结束！");
    }
}
