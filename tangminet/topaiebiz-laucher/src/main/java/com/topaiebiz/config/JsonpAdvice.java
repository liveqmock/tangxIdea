package com.topaiebiz.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice(basePackages = "com.topaiebiz")
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonpAdvice(){
        super("callback","jsonp");
    }
}
