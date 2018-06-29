package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.decorate.component.StyleAuxiliaryVO;
import com.topaiebiz.decorate.constant.Constant;
import org.springframework.stereotype.Component;

@Component
public class StyleAuxiliaryTransformer extends ComponentTransformer<StyleAuxiliaryVO>{
    @Override
    public StyleAuxiliaryVO transform(String content) {
        return JSON.parseObject(content,StyleAuxiliaryVO.class);
    }

    @Override
    public void validate(StyleAuxiliaryVO value) {

    }

    @Override
    public String componentType() {
        return Constant.STYLE_AUXILIARY;
    }
}
