 //微信支付控制层
app.controller('payController' ,function($scope,$location,payService){
    //生成二维码
    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                //总金额和订单号
                $scope.total_fee=(response.total_fee/100).toFixed(2) ;
                $scope.out_trade_no=response.out_trade_no;
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                queryPayStatus();
            }
        )
    };

    //查询支付状态
    queryPayStatus=function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if(response.success){
                    location.href="paysuccess.html#?money="+$scope.total_fee;
                }else {
                    if(response.message=='二维码超时'){
                        $scope.createNative();
                    }else {
                        location.href="payfail.html";
                    }
                }
            }
        )
    };

    //获取金额
    $scope.getMoney=function () {
        return $location.search()['money'];
    };

});	
