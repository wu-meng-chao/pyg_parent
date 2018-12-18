package com.pyg.page.service;
    /*
     * @Desc: 商品详细页接口
     * @Date: 2018/12/3
     */
    public interface ItemPageService {

    /*
     * @Desc: 生成商品详细页
     * @Date: 2018/12/3
     */
    public boolean genItemHtml(Long goodsId);
    
    /* 
     * @Desc: 删除商品详细页
     * @Date: 2018/12/5 
     */
    public boolean deleteItemHtml(Long[] goodsIds);

}
