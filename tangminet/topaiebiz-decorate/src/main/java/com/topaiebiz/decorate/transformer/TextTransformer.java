package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.decorate.component.TextVO;
import com.topaiebiz.decorate.constant.Constant;
import org.springframework.stereotype.Component;

@Component
public class TextTransformer extends ComponentTransformer<TextVO> {
    @Override
    public TextVO transform(String content) {
        return JSON.parseObject(content, TextVO.class);
    }

    @Override
    public void validate(TextVO value) {

    }

    @Override
    public String componentType() {
        return Constant.TEXT;
    }
}
