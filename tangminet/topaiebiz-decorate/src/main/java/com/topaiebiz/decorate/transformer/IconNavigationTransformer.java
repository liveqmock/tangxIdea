package com.topaiebiz.decorate.transformer;


import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.component.IconNavigationVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class IconNavigationTransformer extends ComponentTransformer<IconNavigationVO> {
    @Override
    public IconNavigationVO transform(String content) {
        return JSON.parseObject(content, IconNavigationVO.class);
    }

    @Override
    public void validate(IconNavigationVO value) {
        //图标数量不可为空
        if (null == value.getIconCount()) {
            throw new GlobalException(DecorateExcepionEnum.ICONCOUNT_NOT_NULL);
        }
        //图标信息不可为空
        if (CollectionUtils.isEmpty(value.getImageLinkVOS())) {
            throw new GlobalException(DecorateExcepionEnum.ICONINFO_NOT_NULL);
        }
        //图标数量与图标信息数量必须一致
        if (value.getImageLinkVOS().size() != value.getIconCount()) {
            throw new GlobalException(DecorateExcepionEnum.ICONCOUNT_ERROR);
        }
    }

    @Override
    public String componentType() {
        return Constant.ICON_NAVIGATION;
    }

}
