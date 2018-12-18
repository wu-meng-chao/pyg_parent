app.service("brandService", function ($http) {
    this.findAll=function (id) {
        return $http.get('../brand/findAll.do');
    }
    this.findPage=function (page,rows) {
        return $http.get('../brand/findPage.do?page='+page+'&rows='+rows)
    }
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    };
    this.addBrand = function (entity) {
        return $http.post('../brand/addBrand.do', entity)
    };
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity)
    };
    this.delete = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    };
    this.search = function (page, rows, entity) {
        return $http.post("../brand/search.do?page="+page+"&rows="+rows, entity)
    };
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
});