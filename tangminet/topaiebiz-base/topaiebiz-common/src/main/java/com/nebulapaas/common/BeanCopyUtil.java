package com.nebulapaas.common;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-12 19:12
 */
@Slf4j
public class BeanCopyUtil {
    public interface Convert<S, T> {
        void convert(S src, T item);
    }

    public static void copy(Object src, Object dest) {
        try {
            BeanUtils.copyProperties(dest, src);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static <T> List<T> copyList(Collection<?> source, Class<T> clazz) {
        return copyList(source, clazz, null);
    }

    public static <T> List<T> copyList(Collection<?> source, Class<T> clazz, Convert convert) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        try {
            List<T> records = new ArrayList<T>(source.size());
            for (Object src : source) {
                T item = clazz.newInstance();
                BeanUtils.copyProperties(item, src);
                if (convert != null) {
                    convert.convert(src, item);
                }
                records.add(item);
            }
            return records;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("copy data fail", ex);
        }
    }
}
