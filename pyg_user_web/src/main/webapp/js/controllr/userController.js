 //控制层 
app.controller('userController' ,function($scope,$controller,userService){

    //注册
	$scope.reg=function () {
        if($scope.password!=$scope.entity.password){
            alert("两次输入的密码不一致！");
            $scope.password="";
            $scope.entity.password="";
            return;
        };
        if($scope.code==null||$scope.code==""){
            alert("验证码不能为空！");
            return;
        }
        userService.add($scope.entity,$scope.code).success(
            function (response) {
                alert(response.message)
            }
        );
    };

	//发送验证码
    $scope.entity={'phone':''};
    $scope.sendCode=function () {
        if($scope.entity.phone==null || $scope.entity.phone==''){
            alert("手机号不能为空");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    };

});	
