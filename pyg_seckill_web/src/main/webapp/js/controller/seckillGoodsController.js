 //秒杀商品控制层
app.controller('seckillGoodsController' ,function($scope,$location,seckillGoodsService,$interval){

    //查询正在参与秒杀的商品
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list=response;
                for(var i=0;i<$scope.list.length;i++){
                    $scope.list[i].id=parseInt($scope.list[i].id);
                }
            }
        )
    }

    //从缓存中获取商品实体
    $scope.findOne=function () {
        seckillGoodsService.findOneFromRedis($location.search()['id']).success(
            function (response) {
                $scope.entity=response;

                //倒计时
                allSeconds=Math.floor((new Date(response.endTime).getTime()-new Date().getTime())/1000);
                time=$interval(function () {
                    if(allSeconds>0){
                        allSeconds=allSeconds-1;
                        $scope.timeString=convertTimeString(allSeconds);//时间转换
                    }else {
                        $interval.cancel(time);
                    }
                },1000);
            }
        )
    };

    //毫秒格式化
    convertTimeString=function (allSeconds) {
        var day=Math.floor(allSeconds/(60*60*24));//天数
        var hours=Math.floor((allSeconds-day*60*60*24)/(60*60));//小时数
        var minutes=Math.floor((allSeconds-day*60*60*24-hours*60*60)/(60));//分钟数
        var second=allSeconds -day*60*60*24 - hours*60*60 -minutes*60;//秒数
        var timeStr="";
        if(day>0){
            timeStr=day+"天 ";
        }
        return timeStr+hours+":"+minutes+":"+second;
    };

    //提交订单
    $scope.submitOrder=function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if(response.success){
                    alert("下单成功,请在1分钟内完成支付！");
                    location.href="pay.html";
                }else {
                    alert(response.message);
                }
            }
        )
    }

});	
