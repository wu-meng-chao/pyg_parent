app.controller("searchController",function ($scope,$location ,searchService) {

    //定义搜索对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNum':1,'pageSize':40,'sort':'','sortField':''};

    //搜索
    $scope.search=function () {
        $scope.searchMap.pageNum=parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;//搜索返回的结果
                bulidPageLabel();
            }
        )
    };

    //添加搜索项
    $scope.addSearchItem=function (key, value) {
        if(key=='category'|| key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    };

    //撤销搜索项
    $scope.removeSearchItem=function (key) {
        if(key=='category'|| key=='brand' || key=='price'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    };

    //分页栏
    bulidPageLabel=function () {
        //定义分页栏对象
        $scope.pageLabel=[];
        var firstPage=1;//开始页码
        var lastPage=$scope.resultMap.totalPages;//截止页码
        $scope.firstDot=true;//前省略号
        $scope.lastDot=true;//后省略号
        if(lastPage>=5){//如果总页数大于5
            if($scope.searchMap.pageNum<=3){//如果当前页小于等于3
                lastPage=5;
                $scope.firstDot=false;//前边无点
            }else if($scope.searchMap.pageNum>=(lastPage-2)){//如果当前页大于等于总页数-2
                firstPage=lastPage-4;
                $scope.lastDot=false;//后边无点
            }else {//显示当前页为中心的5页
                firstPage=$scope.searchMap.pageNum-2;
                lastPage=$scope.searchMap.pageNum+2;
            }
        }else{
            $scope.firstDot=false;//前边无点
            $scope.lastDot=false;//前边无点
        }

        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    };

    //分页查询
    $scope.queryByPageNum=function (pageNum) {
        //页码验证
        if(pageNum<1 || pageNum>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNum=pageNum;
        $scope.search();
    };

    //判断当前页是否为第一页
    $scope.isTopPage=function () {
        if($scope.searchMap.pageNum==1){
            return true;
        }else{
            return false;
        }
    };
    //判断当前页是否为最后一页
    $scope.isEndPage=function () {
        if($scope.resultMap!=null && $scope.searchMap.pageNum==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    };

    //排序查询
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    };

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    };

    //接受参数
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    };

    //判断页码是否为当前页
    $scope.isActive=function (page) {
        if($scope.searchMap.pageNum==page){
            return true;
        }else{
            return false;
        }
    }
});