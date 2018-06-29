package com.topaiebiz.file.mgmt.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.file.mgmt.exception.FileMgmtExceptionEnum;
import com.topaiebiz.file.mgmt.service.FileMgmtService;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.message.api.CaptchaApi;
import com.topaiebiz.message.util.CaptchaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Description 文件统一处理控制层
 * <p>
 * Author Aaron.Xue
 * <p>
 * Date 2017年10月8日 上午11:15:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/base/file/mgmt", method = RequestMethod.POST)
public class FileMgmtController {

    @Autowired
    private FileMgmtService fileMgmtService;

    //上传图片
    @MemberLogin
    @RequestMapping(path = "/addOneImage")
    public ResponseInfo addOneImage(@RequestParam("image") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new GlobalException(FileMgmtExceptionEnum.FILE_IS_EMPTY);
        }
        String s = fileMgmtService.uploadFile(file);
        return new ResponseInfo(s);
    }


    //格式化文件路径
    private String getPrefix(MultipartFile file, String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            if (prefix.startsWith("/"))
                prefix = prefix.substring(1, prefix.length());
            if (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        /** 获取文件名，文件后缀。 */
        String suffixName = ".jpg";
        try {
            suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileName = (StringUtils.isBlank(prefix) ? "" : prefix + "/") + UUID.randomUUID().toString() + suffixName;
        /** 校验上传格式是否正确,如果不是jpg,jpeg,png的即不正确。 */
        if (!(".jpg".equalsIgnoreCase(suffixName) || ".jpeg".equalsIgnoreCase(suffixName)
                || ".png".equalsIgnoreCase(suffixName) || ".gif".equalsIgnoreCase(suffixName))) {
            throw new GlobalException(FileMgmtExceptionEnum.FILE_FORMAT_IS_CORRECT);
        }
        return fileName;
    }

    @RequestMapping(path = "/cancelOneImage")
    public ResponseInfo cancelOnePicture(String fileName) {
        /** 将要删除的路径传给，文件服务器。 */
//        try {
//            OssUtils.deleteFile(fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return new ResponseInfo();
    }

    @MemberLogin
    @RequestMapping(path = "/addImages")
    public ResponseInfo addImages(HttpServletRequest request) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        Set<String> strings = fileMgmtService.uploadFiles(files);
        return new ResponseInfo(strings);
    }

//	@RequestMapping(path = "/addBase64Picture", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseInfo addBase64Picture(String base64Array) throws Exception {
//		String module = "goods";
//		String func = "details";
//		if(StringUtils.isEmpty(base64Array)) {
//			throw new GlobalException(FileMgmtExceptionEnum.FILE_IS_EMPTY);
//		}
//		String[] split = base64Array.split("[$]");
//		List<String> list = fileMgmtService.saveBase64Picture(split, FILE_PATH, module, func);
//		return new ResponseInfo(list);
//	}

}
