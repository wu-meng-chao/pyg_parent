//创建模块
var app = angular.module("pyg", []);
//创建过滤器
app.filter("trustHtml",['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);