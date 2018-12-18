 //购物车控制层
app.controller('cartController' ,function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;
                $scope.total={'totalNum':0,'totalMoney':0.00};
                for(var i=0;i<$scope.cartList.length;i++){
                    var cart=$scope.cartList[i];
                    for(var j=0;j<cart.orderItemList.length;j++){
                        var orderItem=cart.orderItemList[j];
                        $scope.total.totalNum=orderItem.num+$scope.total.totalNum;
                        $scope.total.totalMoney+=orderItem.totalFee;
                    }
                }
            }
        )
    };
    
    //添加商品到购物车
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();//刷新列表
                }else{
                    alert(response.message);//弹出错误提示
                }
            }
        )
    };
    
    //获取收货人地址
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList=response;
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    };

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };
    
    //判断当前地址是否被选中
    $scope.isSelectedAddress=function (address) {
        if(address==$scope.address){
            return true;
        }
        return false;
    };

    //订单表
    $scope.order={'paymentType':'1'};
    //选择支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    };

    //保存订单
    $scope.submitOrder=function(){
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder( $scope.order ).success(
            function(response){
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            }
        );
    };

    //定义联系人对象
    $scope.newAddress={};

    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.newAddress.id!=null){//如果有ID
            serviceObject=cartService.update( $scope.newAddress ); //修改
        }else{
            serviceObject=cartService.add( $scope.newAddress);//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    };

    //回显地址
    $scope.findOne=function () {
        cartService.findOne($scope.address.id).success(
            function (response) {
                $scope.newAddress=response;
            }
        )
    };

    //删除
    $scope.del=function () {
        cartService.del($scope.address.id).success(
            function (response) {
                if(response.success){
                    $scope.findAddressList();
                }else {
                    alert(response.message);
                }
            }
        )
    };

    //设置默认地址
    $scope.setDefault=function (type) {
        cartService.setDefault($scope.address.id).success(
            function (response) {
                if(response.success){
                    $scope.findAddressList();
                }else {
                    alert(response.message);
                }
            }
        );
    }

});	
