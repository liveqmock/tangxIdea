package com.topaiebiz.member.repair.dto;

import lombok.Data;

/***
 * @author yfeng
 * @date 2018-02-08 20:19
 */
@Data
public class RepairResultDTO {
    private Long lastRecordId;
    private Integer resultSize;

    public Long getLastRecordId() {
        return lastRecordId;
    }

    public void setLastRecordId(Long lastRecordId) {
        this.lastRecordId = lastRecordId;
    }

    public Integer getResultSize() {
        return resultSize;
    }

    public void setResultSize(Integer resultSize) {
        this.resultSize = resultSize;
    }

    public static RepairResultDTO buildEmptyResult(Long lastRecordId) {
        RepairResultDTO res = new RepairResultDTO();
        res.setLastRecordId(lastRecordId);
        res.setResultSize(0);
        return res;
    }
}