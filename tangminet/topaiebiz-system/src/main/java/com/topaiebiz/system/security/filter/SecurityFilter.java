package com.topaiebiz.system.security.filter;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.security.util.SystemUserCacheKey;
import com.topaiebiz.system.util.CustomerUrlUtil;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.security.constants.SystemMerchantConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Description: session过滤器，判断是否有权限。
 * <p>
 * Author: Aaron.Xue
 * <p>
 * Date: 2017年9月15日 下午5:52:39
 * <p>
 * Copyright: Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class SecurityFilter implements Filter {

	@Autowired
	private RedisCache redisCache;

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		String userLoginId = req.getHeader("userLoginId");
		CurrentUserDto userDto = null;
		if (null != userLoginId && !"".equals(userLoginId)) {
			String userDtoJson = redisCache.get(SystemUserCacheKey.LOGIN_USER_INFO_PREFIX + userLoginId);
			userDto = JSON.parseObject(userDtoJson, CurrentUserDto.class);
			SecurityContextUtils.setCurrentUserDto(userDto);
		}
		//放过允许访问的
		boolean flag = this.checkPermissionURL(uri);
		if (flag) {
			chain.doFilter(request, response);
			return;
		}
		//放过不是平台和商家端的
		flag = this.checkPlatformURL(uri);
		if (flag) {
			chain.doFilter(request, response);
			return;
		}
		//查看权限
		flag = this.isOwn(uri, userLoginId, userDto);
		if (flag) {
			chain.doFilter(request, response);
			return;
		}
		res.getWriter().print("{\"code\":\"-501\"}");

//        if (null != userLoginId && !"".equals(userLoginId)) {
////            flag = true;
//////            flag = this.isOwn(uri, userLoginId);
////            if(flag){
////                chain.doFilter(request, response);
////                return;
////            }else{
////                res.getWriter().print("{\"code\":\"-501\"}");
////            }
////        } else {
////            res.getWriter().print("{\"code\":\"-501\"}");
////            return;
////        }
	}

	//是否拥有权限
	private boolean isOwn(String uri, String userLoginId, CurrentUserDto userDto) {
		if (null == userLoginId || "".equals(userLoginId)) {
			return false;
		}
		if (userDto == null) {
			return false;
		}
		//如果为空则为平台端，跳过
		if (userDto.getMerchantId() != null) {
			String frozen = redisCache.get(SystemMerchantConstants.MERCHANT_FROZED_STATUS+userDto.getMerchantId());
			if (StringUtils.isNotBlank(frozen)) {
				return false;
			}
		}
		//查看权限
		String userResourceJSON = redisCache.get(SystemUserCacheKey.LOGIN_USER_RESOURCE_PREFIX + userLoginId);
		List<String> arrayList = JSON.parseObject(userResourceJSON, ArrayList.class);
//        if(arrayList == null){
//            return false;
//        }else if(arrayList.contains(uri)){
//            return true;
//        }else{
//            for (String preUrl : arrayList) {
//                if(preUrl.contains("{")){
//                    if(preUrl.substring(0, preUrl.indexOf("{")).equals(uri.substring(0, uri.lastIndexOf("/") + 1))){
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
		//模拟true
		return true;
	}

	//允许访问的。true可以放过
	private boolean checkPermissionURL(String uri) {
		//所有平台和商家的
		List<String> merchantURLList = CustomerUrlUtil.getMerchantURLList();
		List<String> platformURLList = CustomerUrlUtil.getPlatformURLList();
		//没在平台和商家
		if (!merchantURLList.contains(uri) && !platformURLList.contains(uri)) {
			for (String preUrl : merchantURLList) {
				if (preUrl.contains("{")) {
					if (preUrl.substring(0, preUrl.indexOf("{")).equals(uri.substring(0, uri.lastIndexOf("/") + 1))) {
						return false;
					}
				}
			}
			for (String preUrl : platformURLList) {
				if (preUrl.contains("{")) {
					if (preUrl.substring(0, preUrl.indexOf("{")).equals(uri.substring(0, uri.lastIndexOf("/") + 1))) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	//放过不是平台和商家的。true可以放过
	private boolean checkPlatformURL(String uri) {
		List<String> permissionURLList = CustomerUrlUtil.getPermissionURLList();
		if (permissionURLList.contains(uri)) {
			return true;
		}
		for (String preUrl : permissionURLList) {
			if (preUrl.contains("{")) {
				if (preUrl.substring(0, preUrl.indexOf("{")).equals(uri.substring(0, uri.lastIndexOf("/") + 1))) {
					return true;
				}
			}
		}
		return false;
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}

