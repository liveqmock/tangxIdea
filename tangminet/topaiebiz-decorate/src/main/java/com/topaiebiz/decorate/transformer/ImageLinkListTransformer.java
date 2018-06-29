package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.component.ImageLinkListVO;
import com.topaiebiz.decorate.component.ImageLinkVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ImageLinkListTransformer extends ComponentTransformer<ImageLinkListVO> {
    @Override
    public ImageLinkListVO transform(String content) {
        return JSON.parseObject(content, ImageLinkListVO.class);
    }

    @Override
    public void validate(ImageLinkListVO value) {
        if (CollectionUtils.isEmpty(value.getImageLinkVOS())) {
            throw new GlobalException(DecorateExcepionEnum.IMAGE_NOT_NULL);
        }
        //文字内容长度≤4
        for (ImageLinkVO imageLinkVO : value.getImageLinkVOS()) {
            if (StringUtils.isNotEmpty(imageLinkVO.getWords())) {
                if (imageLinkVO.getWords().length() > Constant.WORDS_MAX_LENGTH) {
                    throw new GlobalException(DecorateExcepionEnum.WORDS_TOO_LONG);
                }
            }
        }
    }

    @Override
    public String componentType() {
        return Constant.IMAGE_LINK;
    }
}
