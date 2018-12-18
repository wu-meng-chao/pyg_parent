package com.pyg.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    //返回搜索结果
    public Map<String,Object> search(Map searchMap);
    //审核通过后更新索引库
    public void importData(List list);
    //删除后更新索引库
    public void deleteData(List goodsIdList);
}
