package com.nebulapaas.common;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/***
 * @author yfeng
 * @date 2017-12-29 16:56
 */
public class PageDataUtil {

    /**
     * 拷贝分页结果
     *
     * @param srouce 从DAL得到的分页结果
     * @return DTO层的分页模型
     */
    public static <T> PageInfo<T> copyPageInfo(Page<T> srouce) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setRecords(srouce.getRecords());
        int curSize = srouce.getRecords() == null ? 0 : srouce.getRecords().size();
        pageInfo.setCurrentPageSize(curSize);
        pageInfo.setPageNo(srouce.getCurrent());
        pageInfo.setPageSize(srouce.getSize());
        pageInfo.setTotalCount(srouce.getTotal());
        pageInfo.setTotalPage(srouce.getPages());
        return pageInfo;
    }

    public static <T> PageInfo<T> copyPageInfo(Page srouce, Class<T> clazz) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setRecords(copyList(srouce.getRecords(), clazz));
        int curSize = srouce.getRecords() == null ? 0 : srouce.getRecords().size();
        pageInfo.setCurrentPageSize(curSize);
        pageInfo.setPageNo(srouce.getCurrent());
        pageInfo.setPageSize(srouce.getSize());
        pageInfo.setTotalCount(srouce.getTotal());
        pageInfo.setTotalPage(srouce.getPages());
        return pageInfo;
    }

    /**
     * 构建MyBatis分页查询参数对象
     *
     * @param pagePO 服务层的分页入参
     * @return Dal层的分页查询入参
     */
    public static <T> Page<T> buildPageParam(PagePO pagePO) {
        Page<T> page = new Page<>();
        page.setCurrent(pagePO.getPageNo());
        page.setSize(pagePO.getPageSize());
        return page;
    }

    public static <T> List<T> copyList(Collection<?> source, Class<T> clazz) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        try {
            List<T> records = new ArrayList<T>(source.size());
            for (Object src : source) {
                T item = clazz.newInstance();
                BeanCopyUtil.copy(src, item);
                records.add(item);
            }
            return records;
        } catch (Exception ex) {
            throw new RuntimeException("copy data fail", ex);
        }
    }


    public static <T> PageInfo<T> setPageInfo(PageInfo<T> pageInfo, Page page) {
        int curSize = page.getRecords() == null ? 0 : page.getRecords().size();
        pageInfo.setCurrentPageSize(curSize);
        pageInfo.setPageNo(page.getCurrent());
        pageInfo.setPageSize(page.getSize());
        pageInfo.setTotalCount(page.getTotal());
        pageInfo.setTotalPage(page.getPages());
        return pageInfo;
    }
}