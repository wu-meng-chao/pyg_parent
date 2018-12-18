//服务层
app.service('cartService',function($http){
	//查询购物车
	this.findCartList=function(){
		return $http.get('cart/findCartList.do?');
	}
	//添加商品到购物车
    this.addGoodsToCartList=function (itemId, num) {
        return $http.get("cart/addGoodsToCart.do?itemId="+itemId+"&num="+num);
    };
    //获取收货人地址
    this.findAddressList=function () {
        return $http.get("address/findListByLoginUser.do");
    };

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    };

    //添加联系人地址
    this.add=function (address) {
        return $http.post("address/add.do",address);
    };

    //回显
    this.findOne=function (id) {
        return $http.get("address/findOne.do?id="+id);
    };

    //删除
    this.del=function (id) {
        return $http.get("address/delete.do?id="+id);
    };

    //修改联系人地址
    this.update=function (address) {
        return $http.post("address/update.do",address);
    };

    //设置默认地址
    this.setDefault=function (id) {
        return $http.get("address/setDefault.do?id="+id);
    }
});
