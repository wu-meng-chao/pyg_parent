 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
	}
	
	//查询实体 
	$scope.findOne=function(id){
        if(id!=undefined) {
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entry = response;
                    //商品介绍
                    $scope.entry.goodsDesc.itemImages = JSON.parse($scope.entry.goodsDesc.itemImages);
                    $scope.entry.goodsDesc.specificationItems = JSON.parse($scope.entry.goodsDesc.specificationItems);
                    var itemlist = $scope.entry.itemList;
                    for (var i = 0; i < itemlist.length; i++) {
                        itemlist[i].spec = JSON.parse(itemlist[i].spec);
                    }
                }
            );
        }
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//监测搜索条件
	$scope.$watch("searchEntity.auditStatus",function (newValue, oldValue) {
        if(newValue!=undefined){
            $scope.reloadList();
        }
    })

    //商品状态
    $scope.status=['未审核','审核通过','审核未通过','已关闭'];
    //商品分类列表
    $scope.itemCatList=[];
    //查询商品分类
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id ]=response[i].name;
                }
            }
        );
    }

    //批量审核
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();//重新加载页面
                    $scope.selectIds=[];//清空
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //判断规格复选框是否被勾选
    $scope.checkAttributeValue=function (specNAme, optionName) {
        var items=$scope.entity.goodsDesc.specificationItems;
        var object=$scope.searchObjectByKey(items,'attributeName',specNAme);
        if(object!=null){
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    //查询分类名称
    $scope.queryItemCateName=function (id) {
        if(id!=undefined){
            itemCatService.findOne(id).success(
                function (response) {
                    $scope.ItemCateName=response.name;
                }
            )
        }
    }

    
});	
