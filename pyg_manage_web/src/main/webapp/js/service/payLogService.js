//服务层
app.service('payLogService',function($http){

	//搜索
	this.search=function(page,rows,searchEntity,createTime){
		return $http.post('../payLog/search.do?page='+page+"&rows="+rows+"&createTime="+createTime, searchEntity);
	};
});
