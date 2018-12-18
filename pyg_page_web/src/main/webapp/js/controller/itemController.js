app.controller("itemController", function ($scope,$http) {
	
	//定义规格选项对象
	$scope.specItems={};
	//当前选择的SKU
	$scope.sku={};
	
	//数量操作
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	};
   
    //规格选择
	$scope.selectSepcification=function(key,value){
		$scope.specItems[key]=value;
		searchSku();
	};
	
	//判断规格是否被选中
	$scope.isSelected=function(key,value){
		if($scope.specItems[key]==value){
			return true;
		}else{
			return false;
		}
	};
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specItems=JSON.parse(JSON.stringify($scope.sku.spec));
	};
	
	//匹配两个对象
	matchObject=function(map1,map2){
		for(var key in map1){
			if(map1[key]!=map2[key]){
				return false;
			}
		}
		for(var key in map2){
			if(map1[key]!=map2[key]){
				return false;
			}
		}
		return true;
	};
	
	//查询SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec ,$scope.specItems )){
				$scope.sku=skuList[i];
				return ;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	};
	
	//添加商品到购物车
	$scope.addToCart=function(){
		//alert('skuid:'+$scope.sku.id);
        $http.get('http://localhost:9107/cart/addGoodsToCart.do?itemId='
            + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
            function(response){
                if(response.success){
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
                }else{
                    alert(response.message);
                }
            }
        );
	};
	
});




