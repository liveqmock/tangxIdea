package com.nebulapaas.base.po;

import lombok.Setter;

import java.io.Serializable;

/***
 * 分页查询模型
 * @author yfeng
 * @date 2017-12-30 13:49
 */
@Setter
public class PagePO implements Serializable {
    private static final long serialVersionUID = -6011307790633488215L;

    public static final int ONE = 1;
    public static final int DEFAULT_PAGE_SIZE = 15;

    /**
     * 查询页码 开始1
     */
    private int pageNo = ONE;

    /**
     * 每页记录条数
     */
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int maxPageNo;

    public void setMaxPageNo(int maxPageNo) {
        if (maxPageNo < ONE) {
            throw new RuntimeException("maxPage less than 1");
        }
        this.maxPageNo = maxPageNo;
    }

    public int getPageNo() {
        //最小为1
        return pageNo < ONE ? ONE : pageNo;
    }

    public int getPageSize() {
        //最小为1
        int val = pageSize < ONE ? ONE : pageSize;

        //若设置了最大页码且超过最大页码,则使用最大页码
        if (maxPageNo > 0 && val > maxPageNo) {
            val = maxPageNo;
        }
        return val;
    }
}