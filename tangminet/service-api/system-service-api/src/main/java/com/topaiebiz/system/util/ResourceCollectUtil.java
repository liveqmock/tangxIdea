package com.topaiebiz.system.util;

import com.topaiebiz.system.dto.ResourceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜集后台资源URL,用户持久化数据库
 */
public class ResourceCollectUtil {

    //所有需要收集的URL
    private static List<ResourceDto> resourceDtos = new ArrayList<ResourceDto>();

    protected static void addResourceList(ResourceDto resourceDto) {
        resourceDtos.add(resourceDto);
    }

    public static List<ResourceDto> getResourceList() {
        return resourceDtos;
    }

}
