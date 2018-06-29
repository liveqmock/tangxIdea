package com.topaiebiz.member.member.utils;

import java.util.UUID;

/**
 * Created by ward on 2017-12-30.
 */
public class IdGeneratorUtil {

    public IdGeneratorUtil() {
    }

    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(generatorUUID());
    }
}
