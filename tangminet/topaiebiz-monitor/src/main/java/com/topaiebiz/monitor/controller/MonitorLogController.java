package com.topaiebiz.monitor.controller;

import com.nebulapaas.common.DateUtils;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.monitor.dto.MonitorLogDTO;
import com.topaiebiz.monitor.service.MonitorLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.topaiebiz.monitor.exception.MonitorExceptionEnum.GETTING_LOG_CONTENT_FAILURE;

@Slf4j
@RestController
@RequestMapping(path = "/monitor/log", method = RequestMethod.POST)
public class MonitorLogController {
	private int separateIndex = 3;
	//内容长度限制
	private int textLimit = 65535;

	@Autowired
	private MonitorLogService monitorLogService;



	@RequestMapping("/dateRequestCount")
	public ResponseInfo dateRequestCount(@RequestBody String dateTime) {
		if (StringUtils.isBlank(dateTime)) {
			return new ResponseInfo(0L);
		}
		Date time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			time = sdf.parse(dateTime);
			Date endTime = DateUtils.getEndTime(new Date());
			if (time.after(endTime)){
				return new ResponseInfo("请勿输入未来时间");
			}
			/** 获取当天24小时请求总数 **/
			List<Map<String,Long>> countList = monitorLogService.getDayQPSCount(time);
			return new ResponseInfo(countList);
		} catch (ParseException ex) {
			log.error("时间转换异常={}",dateTime);
			return null;
		}




	}

	/**
	 * 测试日志
	 *
	 * @return
	 */
	@RequestMapping("/testWarnLog")
	public ResponseInfo testWarnLog() {
		GlobalException ge = new GlobalException(GETTING_LOG_CONTENT_FAILURE);
		log.info(ge.getMessage(), ge);
		log.warn(ge.getMessage(), ge);
		return new ResponseInfo();
	}

	/**
	 * 测试日志
	 *
	 * @return
	 */
	@RequestMapping("/testLog")
	public ResponseInfo testLog() {
		GlobalException ge = new GlobalException(GETTING_LOG_CONTENT_FAILURE);
		log.error(ge.getMessage(), ge);
		return new ResponseInfo();
	}

	/**
	 * 监控日志记录
	 *
	 * @return
	 */
	@RequestMapping("/error")
	public ResponseInfo log(HttpServletRequest request) {
		MonitorLogDTO monitorLog = new MonitorLogDTO();
		try {
			String loggerName = request.getParameter("loggerName");
			monitorLog.setModuleName(getModuleName(loggerName));

			String content = request.getParameter("data");
			monitorLog.setContent(getContent(content));
			monitorLogService.publishLog(monitorLog);
		} catch (Exception e) {
			throw new GlobalException(GETTING_LOG_CONTENT_FAILURE);
		}
		return new ResponseInfo();
	}

	/**
	 * 获取模块名称
	 *
	 * @param loggerName
	 * @return
	 */
	private String getModuleName(String loggerName) {
		//第三个"."分隔符之前的字符串
		int j = 0;
		//取第一个满足条件的下标值
		int i = loggerName.indexOf(".");
		for (int k = 0; k < separateIndex; k++) {
			//放在indexOf之前，防止截取字符串异常
			j = i;
			i = loggerName.indexOf(".", i + 1);
			//取整个字符串
			if (i == -1 && k < separateIndex - 1) {
				j = loggerName.length();
				break;
			}
		}
		return loggerName.substring(0, j);
	}

	/**
	 * 获取文本内容，长度不能超过数据库的长度限制
	 *
	 * @param content
	 * @return
	 */
	private String getContent(String content) {
		if (content != null
				&& content.length() > textLimit) {
			return content.substring(0, textLimit);
		} else {
			return content;
		}
	}
}
