package com.topaiebiz.promotion.common.util;

import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/***
 * @author yfeng
 * @date 2017-12-29 16:56
 */
public class PageUtil {

    /**
     * 拷贝分页结果
     *
     * @param source 从DAL得到的分页结果
     * @return DTO层的分页模型
     */
    public static <T> Page<T> copyPage(Page<?> source, Class<T> clazz) {
        try {
            Page pageInfo = new Page();
            BeanUtils.copyProperties(source, pageInfo);
            List resource = source.getRecords();
            if (CollectionUtils.isEmpty(resource)) {
                return pageInfo;
            }

            //拷贝集合对象
            List<T> records = copyList(resource, clazz);
            pageInfo.setRecords(records);
            return pageInfo;
        } catch (Exception ex) {
            throw new RuntimeException("copy data fail", ex);
        }
    }

    public static <T> List<T> copyList(Collection<?> source, Class<T> clazz) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        try {
            List<T> records = new ArrayList<T>(source.size());
            for (Object src : source) {
                T item = clazz.newInstance();
                BeanUtils.copyProperties(src, item);
                records.add(item);
            }
            return records;
        } catch (Exception ex) {
            throw new RuntimeException("copy data fail", ex);
        }
    }

}