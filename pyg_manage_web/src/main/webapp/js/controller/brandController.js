app.controller("brandController", function ($scope,$controller, brandService) {

    $controller("baseController",{$scope:$scope});

    //查询所有
    $scope.findAll=function(){
        brandService.findAll().success(
            function(response){
                $scope.list=response;
            }
        )
    }

    //分页查询
    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(
            function (response) {//分页结果集
                $scope.list=response.rows;
                //total应该给angular分页组件
                $scope.paginationConf.totalItems=response.total;
            }
        );
    }

    //回显
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    //添加和修改
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.addBrand($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    //删除
    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        );
    };

    //条件分页查询
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }
        );
    }
})