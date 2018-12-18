//服务层
app.service('seckillGoodsService',function($http){
    //查询正在参与秒杀的商品
    this.findList=function () {
        return $http.get("seckillGoods/findList.do");
    };

    //从缓存中获取商品实体
    this.findOneFromRedis=function (id) {
        return $http.get("seckillGoods/findOneFromRedis.do?id="+id);
    };

    //提交订单
    this.submitOrder=function (seckillId) {
        return $http.get("seckillOrder/submitOrder.do?seckillId="+seckillId);
    }
});
