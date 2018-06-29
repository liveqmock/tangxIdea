package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.*;

/***
 * @author yfeng
 * @date 2018-03-05 13:59
 */
@Component("haitaoValidateHandler")
public class HaitaoValidateHandler implements OrderSubmitHandler {

    /**
     * 身份证格式: 666666 yyyyMMdd 555 x|X
     */
    private static Pattern idNumPattern = Pattern.compile("^\\d{6}(18|19|20)?\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|[xX])$");

    private static boolean isValidIdNum(String idNum) {
        return idNumPattern.matcher(idNum).matches();
    }

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        if (!submitContext.isHasHaitaoOrder()) {
            return;
        }
        //校验身份证信息
        if (StringUtils.isBlank(orderRequest.getIdNum())) {
            throw new GlobalException(ID_NUMBER_BLANK_ERROR);
        }
        //身份证格式不正确
        if (!isValidIdNum(orderRequest.getIdNum())) {
            throw new GlobalException(ID_NUMBER_NOT_VALID_ERROR);
        }

        //零时为发布做此开关，后期去掉
        //校验姓名信息
        if (StringUtils.isBlank(orderRequest.getBuyerName())) {
            throw new GlobalException(BUYER_NAME_BLANK_ERROR);
        }
    }
}