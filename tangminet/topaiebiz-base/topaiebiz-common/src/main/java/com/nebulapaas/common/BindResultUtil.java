package com.nebulapaas.common;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import org.springframework.validation.BindingResult;

/***
 * @author yfeng
 * @date 2018-01-05 15:29
 */
public class BindResultUtil {

    public static void  dealBindResult(BindingResult result){
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
    }
}
