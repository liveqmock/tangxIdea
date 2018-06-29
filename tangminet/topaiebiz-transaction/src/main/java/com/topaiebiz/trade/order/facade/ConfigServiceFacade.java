package com.topaiebiz.trade.order.facade;

import com.google.common.collect.Lists;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.trade.order.dto.pay.PayConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/***
 * @author yfeng
 * @date 2018-01-18 19:57
 */
@Slf4j
@Component
public class ConfigServiceFacade {
    private static String POINTS_ORDER_MAX = "points_ordermax";
    private static String POINTS_ORDER_RATE = "points_orderrate";
    private static String CARD_PAY_STORES_BLACK_LIST = "no_cards_pay_store";

    private static Pattern SPLIT_PATTERN = Pattern.compile(",");

    @Autowired
    private ConfigApi configApi;

    public PayConfiguration getPayConfig() {
        PayConfiguration payConfig = new PayConfiguration();

        //礼卡支付店铺黑名单
        Set<Long> storeBlackList = getCardStoreBlackList();
        payConfig.setCardStoreBlackList(storeBlackList);

        return payConfig;
    }

    /***
     * 礼卡消费店铺黑名单
     * @return
     */
    public Set<Long> getCardStoreBlackList() {
        return getIdSetConfig(CARD_PAY_STORES_BLACK_LIST);
    }

    private Set<Long> getIdSetConfig(String code) {
        String val = getConfig(code);
        if (StringUtils.isBlank(val)) {
            return SetUtils.emptySet();
        }
        //字符串内容分割
        String[] valArray = SPLIT_PATTERN.split(val);
        List<String> strList = Lists.newArrayList(valArray);
        Set<Long> valSet = new HashSet<>();
        if (CollectionUtils.isEmpty(strList)) {
            return valSet;
        }

        //内容转换
        for (String item : strList) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            try {
                Long longVal = Long.parseLong(item);
                valSet.add(longVal);
            } catch (NumberFormatException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        return valSet;
    }

    private String getConfig(String code) {
        String content = configApi.getConfig(code);
        if (StringUtils.isBlank(content)) {
            return "";
        }
        return content.trim();
    }
}