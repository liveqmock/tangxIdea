package com.topaiebiz.goods.repair.service;


import com.topaiebiz.goods.repair.dto.RepairResultDTO;

import java.util.Map;

/**
 * Created by hecaifeng on 2018/3/26.
 */
public interface CommentRepairService {


    void addCommentItemId();

    RepairResultDTO fixSkuCommoentData(Integer num, Long startId, Map<Long, String> categoryMap);

    long removeCommentRedis();
}
