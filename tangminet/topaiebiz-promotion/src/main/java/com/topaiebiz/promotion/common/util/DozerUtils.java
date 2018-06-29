package com.topaiebiz.promotion.common.util;

import org.dozer.DozerBeanMapper;
import org.dozer.util.MappingValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象间转化工具类
 */
public final class DozerUtils {

    private static DozerBeanMapper dozerMapper = new DozerBeanMapper();

    /**
     * list->list
     *
     * @param source
     * @param destinationClass
     * @return
     */
    public static List maps(final List source, Class destinationClass) {
        List desList = new ArrayList<>();
        if (source == null)
            return desList;
        MappingValidator.validateMappingRequest(source, destinationClass);
        for (Object src : source) {
            Object des = dozerMapper.map(src, destinationClass);
            desList.add(des);
        }
        return desList;
    }

    /**
     * object-.object
     *
     * @param source
     * @param destinationClass
     * @param <T>
     * @return
     */

    public static <T> T map(final Object source, Class<T> destinationClass) {
        if (source == null)
            return null;
        MappingValidator.validateMappingRequest(source, destinationClass);
        return dozerMapper.map(source, destinationClass);
    }

}