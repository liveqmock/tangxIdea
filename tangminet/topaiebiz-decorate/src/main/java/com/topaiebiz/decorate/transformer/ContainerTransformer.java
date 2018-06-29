package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.decorate.component.ContainerVO;
import com.topaiebiz.decorate.constant.Constant;
import org.springframework.stereotype.Component;

@Component
public class ContainerTransformer extends ComponentTransformer<ContainerVO> {
    @Override
    public ContainerVO transform(String content) {
        return JSON.parseObject(content,ContainerVO.class);
    }

    @Override
    public void validate(ContainerVO value) {

    }

    @Override
    public String componentType() {
        return Constant.CONTAINER;
    }
}
