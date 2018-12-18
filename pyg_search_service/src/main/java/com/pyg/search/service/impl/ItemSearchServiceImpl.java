package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object> map=new HashMap<>();
        // 1、高亮
        map.putAll(searchList(searchMap));
        //2、分组查询
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //3、品牌和规格
        String category = (String) searchMap.get("category");
        if(!category.equals("")){
            map.putAll(searchBrandAndSpecList(category));
        }else{
            if(categoryList.size()>0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }

    /*
     * @Desc: 修改后更新索引库
     * @Date: 2018/12/2
     */
    @Override
    public void importData(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /*
     * @Desc: 删除后更新索引库
     * @Date: 2018/12/2
     */
    @Override
    public void deleteData(List goodsIdList) {
        SolrDataQuery query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
        System.out.println("删除商品ID:"+goodsIdList);
    }

    /** 
    * @Desc: /返回一个高亮的查询结果
    * @Date: 2018/11/30 
    */
    private Map<String, Object> searchList(Map searchMap) {

        Map<String,Object> map=new HashMap<>();

        //【创建高亮查询对象】
        HighlightQuery query=new SimpleHighlightQuery();

        //【构建高亮查询选项对象】
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>"); //后缀
        query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项

        //1.1 【关键字查询】
        String keywords = (String) searchMap.get("keywords");
        //关键字空格处理
        searchMap.put("keywords",keywords.replace(" ","" ));
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);//为查询对象设置查询条件

        //1.2 【商品分类过滤】
        if(!searchMap.get("category").equals("")) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3 【品牌过滤】
        if(!searchMap.get("brand").equals("")) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4 【规格过滤】
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5 【价格区间过滤】
        if(!searchMap.get("price").equals("")){
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果最低价不是0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果最高价不是*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        //1.6 【分页】
        Integer pageNum = (Integer) searchMap.get("pageNum");//提取当前页码
        if(pageNum==null){
            pageNum=1;//设置默认为第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//提取每页记录数
        if(pageSize==null){
            pageSize=20;//设置默认为20条
        }
        query.setOffset((pageNum-1)*pageSize);//设置起始索引
        query.setRows(pageSize);

        //1.7 【排序】
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if(sortValue!=null&&!sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        //【执行查询，获取高亮页对象】
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //【高亮入口集合】
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

        //【循环高亮入口集合】
        for(HighlightEntry<TbItem> h: entryList){//循环高亮入口集合
            TbItem item = h.getEntity();//获取原实体类
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages",page.getTotalPages() );//总页数
        map.put("total",page.getTotalElements() );//总记录数
        return map;
    }

    /**
    * @Desc: /查询分类列表
    * @Param: [searchMap]
    * @return: java.util.List
    * @Date: 2018/11/30
    */
    private List<String> searchCategoryList(Map searchMap){
        List<String> list=new ArrayList<String>();
        Query query=new SimpleQuery("*:*");

        //【创建查询条件(关键字查询)】
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //【设置分组选项】
        GroupOptions groupOptions =new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //【获取分组页对象】
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //【获取分组结果对象】
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //【获取分组结果入口页】
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //【获取分组入口集合】
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //【得到分组结果的名称】
            String groupValue = entry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }


    /**
    * @Desc:  查询品牌和规格列表
    * @Param: [category]
    * @return: java.util.Map
    * @Date: 2018/12/1
    */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        Long categoryId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(categoryId!=null){
            //【查询品牌列表】
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(categoryId);
            map.put("brandList",brandList);
            //【查询规格列表】
            List specList = (List) redisTemplate.boundHashOps("specList").get(categoryId);
            map.put("specList",specList );
        }
        return map;
    }
}
