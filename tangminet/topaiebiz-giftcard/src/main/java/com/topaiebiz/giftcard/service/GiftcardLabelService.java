package com.topaiebiz.giftcard.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.giftcard.entity.GiftcardLabel;
import com.topaiebiz.giftcard.vo.GiftcardLabelVO;
import com.topaiebiz.giftcard.vo.LabelShowVO;

import java.util.List;

/**
 * @description: 礼卡标签服务
 * @author: Jeff Chen
 * @date: created in 下午3:06 2018/1/12
 */
public interface GiftcardLabelService extends IService<GiftcardLabel>{

    /**
     * 批量逻辑删除
     * @param ids
     * @return
     */
    Boolean batchDeleteByIds(List<Long> ids,String modifier);

    /**
     * 查询列表标签
     *
     * @param pagePo
     * @param labelName
     * @return
     */
    PageInfo<GiftcardLabelVO> queryGiftcardLabel(PagePO pagePo, String labelName);
    /**
     * 展示标签列表
     * @return
     */
    List<LabelShowVO> showLabelList();

    /**
     * 所有可用标签类别
     * @return
     */
    List<LabelShowVO> allLabelList();
}
