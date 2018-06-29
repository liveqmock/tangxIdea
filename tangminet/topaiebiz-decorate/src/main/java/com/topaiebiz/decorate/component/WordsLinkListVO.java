package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

@Data
public class WordsLinkListVO {

    private String backgroundImage;

    private String logoImage;

    private List<WordsLinkVO> wordsLinkVOS;

}
