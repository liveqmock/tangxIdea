package com.nebulapaas.base.model;
import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 * 基于页码的分页模型
 * @author yfeng
 * @date 2017-12-29 15:00
 */
@Data
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = -6011307790633488215L;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 每页数量
     */
    private Integer currentPageSize;

    /**
     * 总记录条数
     */
    private Integer totalCount;

    /**
     * 总记页数
     */
    private Integer totalPage;

    /**
     * 数据内容
     */
    private List<T> records;


    public PageInfo() {
        this.pageNo = PagePO.ONE;
        this.pageSize = PagePO.DEFAULT_PAGE_SIZE;
        this.currentPageSize = 0;
        this.totalCount = 0;
        this.totalPage = 0;
        this.records = Collections.emptyList();
    }
}