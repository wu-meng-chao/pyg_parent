app.service('contentService',function ($http) {

    //查询轮播图列表
    this.findByCategoryId=function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
    }
})