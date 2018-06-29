package com.topaiebiz.context;

import com.topaiebiz.model.RequestInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-31 9:23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestInfoContext {
    private static ThreadLocal<RequestInfo> context = new ThreadLocal<RequestInfo>();

    public static void set(RequestInfo info) {
        context.set(info);
    }

    public static RequestInfo get() {
        return context.get();
    }

    public static void clean() {
        context.remove();
    }
}
