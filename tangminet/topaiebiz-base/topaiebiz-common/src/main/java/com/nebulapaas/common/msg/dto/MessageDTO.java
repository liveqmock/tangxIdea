package com.nebulapaas.common.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-02-01 20:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 业务参数
     */
    private Map<String, Object> params = new HashMap();

    /**
     * 消息时间
     */
    private Date time = new Date();
}