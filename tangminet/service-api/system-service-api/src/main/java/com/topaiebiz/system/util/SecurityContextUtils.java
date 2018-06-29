package com.topaiebiz.system.util;

import com.topaiebiz.system.dto.CurrentUserDto;


public class SecurityContextUtils {

    private static ThreadLocal<CurrentUserDto> context = new ThreadLocal<CurrentUserDto>();

    public static CurrentUserDto getCurrentUserDto() {
        return context.get();
    }

    public static void setCurrentUserDto(CurrentUserDto currentUserDto){
        context.set(currentUserDto);
    }
}