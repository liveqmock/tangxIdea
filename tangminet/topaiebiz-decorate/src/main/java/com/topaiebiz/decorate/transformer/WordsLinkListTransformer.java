package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.component.WordsLinkListVO;
import com.topaiebiz.decorate.component.WordsLinkVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WordsLinkListTransformer extends ComponentTransformer<WordsLinkListVO> {
    @Override
    public WordsLinkListVO transform(String content) {
        return JSON.parseObject(content, WordsLinkListVO.class);
    }

    @Override
    public void validate(WordsLinkListVO value) {
        if (CollectionUtils.isEmpty(value.getWordsLinkVOS())) {
            throw new GlobalException(DecorateExcepionEnum.WORDS_INFO_NOT_NULL);
        }
        for (WordsLinkVO wordsLinkVO : value.getWordsLinkVOS()) {
            if (StringUtils.isNotEmpty(wordsLinkVO.getWords())) {
                if (wordsLinkVO.getWords().length() > Constant.CONTENT_MAX_LENGTH) {
                    throw new GlobalException(DecorateExcepionEnum.WORDS_TOO_LONG);
                }
            }
        }
    }

    @Override
    public String componentType() {
        return Constant.HEADLINE;
    }
}
