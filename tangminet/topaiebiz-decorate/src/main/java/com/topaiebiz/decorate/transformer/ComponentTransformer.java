package com.topaiebiz.decorate.transformer;

import com.topaiebiz.decorate.dto.ComponentContentDto;

public abstract class ComponentTransformer<T> {

    public abstract <T> T transform(String content);

    public abstract void validate(T value);

    public abstract String componentType();

    public String getPageContent(String dbConent) {
        return dbConent;
    }

    public void dealItem(ComponentContentDto componentContentDto) {

    }
}