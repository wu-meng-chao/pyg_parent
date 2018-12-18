 //控制层 
app.controller('payLogController' ,function($scope,$controller,payLogService){
	
	$controller('baseController',{$scope:$scope});//继承

    $scope.tradeStat=['未支付','已支付'];//交易状态
    $scope.searchEntity={};//定义搜索对象
    
    //获取时间
    $scope.getTime=function () {
        var date=new Date();
        if(date.getMonth()+1>=3){
            $scope.time0 = date.getFullYear()+"-"+(date.getMonth()-2)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        }else {
            $scope.time0 = (date.getFullYear()-1)+"-"+(date.getMonth()+9)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        }
        if(date.getMonth()+1>=6){
            $scope.time1 = date.getFullYear()+"-"+(date.getMonth()-5)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        }else {
            $scope.time1 = (date.getFullYear()-1)+"-"+(date.getMonth()+6)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        }
        $scope.time2 = date.getFullYear()+"-"+(date.getMonth()-11)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    };



	//搜索
	$scope.search=function(page,rows){			
		payLogService.search(page,rows,$scope.searchEntity,$scope.createTime).success(
			function(response){
				$scope.list=response.rows;
				for(var i=0;i<$scope.list.length;i++){
                    $scope.list[i].tradeState=parseInt($scope.list[i].tradeState);
                }
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

    //监测日期下拉列表
    $scope.$watch("createTime",function (newValue, oldValue) {
        if(newValue!=null){
            $scope.reloadList();
            var date=new Date();
            $scope.getTime()
        }
    });

    //监测支付状态下拉列表
    $scope.$watch("searchEntity.tradeState",function (newValue, oldValue) {
        if(newValue!=null){
            $scope.reloadList();
        }
    });

    //监测用户名下拉列表
    $scope.$watch("searchEntity.userId",function (newValue, oldValue) {
        if(newValue!=null){
            $scope.reloadList();
        }
    });
});	
