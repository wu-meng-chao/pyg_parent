package com.pyg.manage.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")//查询全部品牌
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")//分页
    public PageResult findPage(int page, int rows){
        return brandService.findPage(page,rows);
    }

    @RequestMapping("addBrand")//添加
    public Result addBrand(@RequestBody TbBrand tbBrand){
        try {
            brandService.addBrand(tbBrand);
            return new Result(true,"success");
        }catch (Exception e){
            return new Result(false,"添加失败，请稍后重试！");
        }
    }
    @RequestMapping("findOne")//回显
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("update")//修改
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"success");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败，请稍后重试！");
        }
    }

    @RequestMapping("delete")//批量删除
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"success");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败，请稍后重试！");
        }
    }

    @RequestMapping("search")//条件查询
    public PageResult search(@RequestBody TbBrand brand,int page,int rows){
        return brandService.search(brand,page,rows);
    }
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }
}