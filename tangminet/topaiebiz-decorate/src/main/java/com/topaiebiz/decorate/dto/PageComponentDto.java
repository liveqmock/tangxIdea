package com.topaiebiz.decorate.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 页面组件dto
 *
 * @author huzhenjia
 * @since 2018/03/27
 */
@Data
public class PageComponentDto {


    private Long pageId;//关联页面id

    private Date startTime;//发布时间

    private Date endTime;//结束时间

    private List<ComponentDto> componentDtos;//组件信息
}
