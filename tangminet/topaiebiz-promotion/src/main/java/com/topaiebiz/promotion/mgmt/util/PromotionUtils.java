package com.topaiebiz.promotion.mgmt.util;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.ActiveConfigDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionLogEntity;
import com.topaiebiz.promotion.promotionEnum.PromotionPlatformCouponStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 9:58 2018/5/4
 * @Modified by:
 */
@Slf4j
public class PromotionUtils {

	public static final String key = "25d0a8ea02c2d5c4";

	/**
	 * @param userName operationType
	 * @Author: tangx.w
	 * @Description: 插入操作日志记录
	 * @Date: 2018/4/27 15:24
	 */
	public static PromotionLogEntity packagePromotionLog(String userName, Integer operationType, String memo, Long promotionId) {
		PromotionLogEntity promotionLog = new PromotionLogEntity();
		promotionLog.setCreatedTime(new Date());
		promotionLog.setMemo(memo);
		promotionLog.setPromotionId(promotionId);
		promotionLog.setOperationUser(userName);
		promotionLog.setOperationType(operationType);
		return promotionLog;
	}

	public static List<Long> stringToList(String strs) {
		String[] str = strs.split(",");
		List<Long> list = new ArrayList<>();
		for (int i = 0; i < str.length; i++) {
			list.add(Long.valueOf(str[i]));
		}
		return list;
	}

	public static Date pareTime(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(time);
	}

	public static String formatTime(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static Integer judgmentStoreTime(String startTime, String endTime) throws ParseException {
		Date now = new Date();
		Date startDate = pareTime(startTime);
		Date endDate = pareTime(endTime);
		if (now.getTime() < startDate.getTime()) {
			return PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode();
		} else if (now.getTime() > endDate.getTime()) {
			return PromotionStateEnum.PROMOTION_STATE_HAS_ENDED.getCode();
		} else {
			return PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode();
		}
	}

	public static Integer judgmentPlatformTime(Date endTime) {
		Date now = new Date();
		if (now.getTime() > endTime.getTime()) {
			return PromotionPlatformCouponStateEnum.PROMOTION_STATE_HAS_ENDED.getCode();
		} else {
			return PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode();
		}
	}

	public static String packageActiveConfigDto(PromotionDto promotionDto) {
		ActiveConfigDto activeConfigDto = new ActiveConfigDto();
		activeConfigDto.setDayConfineAmount(promotionDto.getDayConfineAmount());
		activeConfigDto.setReceiveType(promotionDto.getReceiveType());
		activeConfigDto.setUserType(promotionDto.getUserType());
		activeConfigDto.setShareConfinePeopleAmount(promotionDto.getShareConfinePeopleAmount());
		activeConfigDto.setNumberOfCopies(promotionDto.getNumberOfCopies());
		activeConfigDto.setReceiveConfineAmount(promotionDto.getReceiveConfineAmount());
		activeConfigDto.setDayConfineAmount(promotionDto.getDayConfineAmount());
		return JSON.toJSONString(activeConfigDto);
	}

	public static Map<Long, CouponDto> couponListToMap(List<CouponDto> couponList) {
		Map<Long, CouponDto> map = new HashMap<>();
		for (CouponDto coupon : couponList) {
			map.put(coupon.getCouponId(), coupon);
		}
		return map;
	}

	/**
	 * DES加密
	 *
	 * @param str
	 * @return
	 */
	public static String getDesEncrypt(String str) {
		byte[] doFinal = null;
		try {
			byte[] decodeHex = Hex.decodeHex(key.toCharArray());
			// 生成密钥对象
			SecretKeySpec secretKeySpec = new SecretKeySpec(decodeHex, "DES");
			// 获取加解密实例
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// 初始化加密模式
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			// 加密
			doFinal = cipher.doFinal(str.getBytes());
		} catch (Exception e) {
			log.error("优惠券分享活动des加密异常====={}", e);
		}
		return new HexBinaryAdapter().marshal(doFinal);
	}


	/**
	 * DES解密
	 *
	 * @param str
	 * @return
	 */
	public static String getDesDecrypt(String str) {
		byte[] doFinal = null;
		try {
			byte[] decodeHex = Hex.decodeHex(key.toCharArray());
			// 生成密钥对象
			SecretKeySpec secretKeySpec = new SecretKeySpec(decodeHex, "DES");
			// 获取加解密实例
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// 初始化解密模式
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			// 解密
			doFinal = cipher.doFinal(new HexBinaryAdapter().unmarshal(str));
		} catch (Exception e) {
			log.error("优惠券分享活动des解密异常====={}", e);
		}
		return new String(doFinal);
	}

	public static Map<Long, BigDecimal> PromotionListTomap(List<PromotionEntity> promotionList) {
		Map<Long, BigDecimal> map = new HashMap<>();
		for (PromotionEntity promotion : promotionList) {
			map.put(promotion.getId(), promotion.getDiscountValue());
		}
		return map;
	}


	/**
	 * 隐去手机号
	 **/
	public static String hiddenPhoneNum(String phoneNum) {
		return StringUtils.isBlank(phoneNum) ? null : phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length() - 4);
	}


	public static StringBuilder listToString(List<Long> list) {
		StringBuilder strs = new StringBuilder();
		strs.append(list.get(0).toString());
		for (int i = 1; i < list.size(); i++) {
			strs = strs.append(",").append(list.get(i).toString());
		}
		return strs;

	}

	public static void  sortSinglePromotions(List<PromotionDTO> promotionDTOs,Long skuId){

	}
}
