 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){

    //继承
	$controller('baseController',{$scope:$scope});
    //商品状态
    $scope.status=['未审核','已审核','审核未通过','关闭'];
    //商品分类列表
    $scope.itemCatList=[];
    //定义页面实体结构
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
    //定义搜索对象
    $scope.searchEntity={};
    //上下架状态
    $scope.isMarketable=['已下架','已上架'];
	
	//查询实体 
	$scope.findOne=function(id){
        var id= $location.search()['id'];//获取参数值
        if(id!=undefined) {
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    //向富文本编辑器添加商品介绍
                    editor.html($scope.entity.goodsDesc.introduction);
                    $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                    $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                    var itemlist = $scope.entity.itemList;
                    for(var i=0;i<itemlist.length;i++){
                        itemlist[i].spec = JSON.parse(itemlist[i].spec);
                    }
                }
            );
        }
	}
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
        goodsService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
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
    $scope.$watch("searchEntity.isMarketable",function (newValue, oldValue) {
        if(newValue!=undefined){
            $scope.reloadList();
        }
    })
	
	//保存
	$scope.save=function(){				
		var serviceObject;//服务层对象
        $scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
                    location.href="goods.html";//跳转到商品列表页
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//上传文件
    $scope.uploadFile=function () {
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    $scope.image_entity.url=response.message;
                    document.getElementById("file").value='';
                }else{
                    alert(response.message);
                }
            }
        )
    }

    //添加图片列表
    $scope.add_image_entity=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //列表中移除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //读取一级分类
    $scope.selectItemCatList1=function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList1=response;
            }
        );
    }
    //读取二级列表
    $scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
        if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCatList2=response;
                }
            )
        }
    });
    //读取三级列表
    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        if(newValue!=undefined) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCatList3 = response;
                }
            )
        }
    });
    //更新模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
        if(newValue!=undefined) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId;
                }
            )
        }
    });

    //监测模板id变化，更新品牌列表，更新规格列表
    $scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
        if(newValue!=undefined) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    /*$scope.typeTemplateId=response;//获取类型模板
                    $scope.typeTemplateId.brandIds=JSON.parse($scope.typeTemplateId.brandIds);*/
                    $scope.typeTemplate = response;//获取类型模板
                    $scope.typeTemplate.brandIds = JSON.parse(response.brandIds);
                }//获取品牌列表
            );
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );
        }
    });

    //保存选中的规格选项
    $scope.updateSpecAttribute=function ($event,text,value) {
        //查看specificationItems中是否存在text
        var object=$scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems,'attributeName',text
        );
        //添加或删除复选框
        if(object!=null){
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
                if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object),1
                    );
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName":text,"attributeValue":[value]}
            )
        }
    }

    //添加列值
    addColumn=function (list, columnName, columnValues) {
        var newList=[];
        for(var i=0;i<list.length;i++){
            var oldRow=list[i];
            for(var j=0;j<columnValues.length;j++){
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
    //创建SKU列表
    $scope.createItemList=function () {
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
        var items=$scope.entity.goodsDesc.specificationItems;
        for(var i=0;i<items.length;i++){
            $scope.entity.itemList=addColumn(
                $scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
        }
    }

    //加载商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
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

    //商品上下架
    $scope.updateIsMarketable=function (id,isMarketable) {
        goodsService.updateIsMarketable(id,isMarketable).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }
});	
