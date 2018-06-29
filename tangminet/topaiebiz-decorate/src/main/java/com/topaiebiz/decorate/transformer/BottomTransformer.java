package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.component.BottomVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class BottomTransformer extends ComponentTransformer<BottomVO> {
    @Override
    public BottomVO transform(String content) {
        return JSON.parseObject(content, BottomVO.class);
    }

    @Override
    public void validate(BottomVO value) {
        if (CollectionUtils.isEmpty(value.getLogoVOS())) {
            throw new GlobalException(DecorateExcepionEnum.BOTTOM_TAB_NOT_NULL);
        }
    }

    @Override
    public String componentType() {
        return Constant.BOTTOM;
    }
}
