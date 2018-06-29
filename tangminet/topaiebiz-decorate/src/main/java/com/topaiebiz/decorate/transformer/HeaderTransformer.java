package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.decorate.component.HeaderVO;
import com.topaiebiz.decorate.constant.Constant;
import org.springframework.stereotype.Component;

@Component
public class HeaderTransformer extends ComponentTransformer<HeaderVO> {
    @Override
    public HeaderVO transform(String content) {
        return JSON.parseObject(content, HeaderVO.class);
    }

    @Override
    public void validate(HeaderVO value) {

    }

    @Override
    public String componentType() {
        return Constant.HEADER;
    }
}
