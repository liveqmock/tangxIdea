package com.topaiebiz.file.mgmt.service.impl;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.file.mgmt.exception.FileMgmtExceptionEnum;
import com.topaiebiz.file.mgmt.service.FileMgmtService;
import com.topaiebiz.file.mgmt.util.OssUtils;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class FileMgmtServiceImpl implements FileMgmtService {

    @Override
    public String uploadFile(MultipartFile file) {
        //校验后缀
        String suffixName;
        try {
            suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } catch (Exception e) {
            suffixName = ".jpg";
            e.printStackTrace();
        }
        if (!(".jpg".equalsIgnoreCase(suffixName) || ".jpeg".equalsIgnoreCase(suffixName)
                || ".png".equalsIgnoreCase(suffixName) || ".gif".equalsIgnoreCase(suffixName))) {
            throw new GlobalException(FileMgmtExceptionEnum.FILE_FORMAT_IS_CORRECT);
        }

        String filePath = OssUtils.getEnvDir();

        //当前登录用户
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        if (currentUserDto == null) {
            //c端
            Long memberId = MemberContext.tryGetMemberId();
            if (memberId == null) {
                //匿名
                filePath = StringUtils.join(filePath, "anonymity/" );
            } else {
                filePath = StringUtils.join(filePath, "customer/", memberId ,"/");
            }
        } else if (SystemUserType.MERCHANT.equals(currentUserDto.getType())) {
            //商家
            filePath = StringUtils.join(filePath, "shop/store/goods/", currentUserDto.getMerchantId(), "/");
        } else if (SystemUserType.ENTER.equals(currentUserDto.getType())) {
            //商家
            filePath = StringUtils.join(filePath, "enter/");
        } else {
            //平台
            filePath = StringUtils.join(filePath, "platform/");
        }


        String fileName = StringUtils.join(filePath, UUID.randomUUID().toString(), suffixName);
        /** 校验上传格式是否正确,如果不是jpg,jpeg,png的即不正确。 */
        try {
            return OssUtils.fileUpload(file.getBytes(), fileName);
        } catch (Exception e) {
            throw new GlobalException(FileMgmtExceptionEnum.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public Set<String> uploadFiles(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            throw new GlobalException(FileMgmtExceptionEnum.FILE_IS_EMPTY);
        }
        Set<String> keys = new HashSet();
        files.forEach(file -> {
            keys.add(this.uploadFile(file));
        });
        return keys;
    }

    @Override
    public String uploadFile(byte[] data, String newFileName) {
        if(data == null || data.length==0){
            throw new GlobalException(FileMgmtExceptionEnum.FILE_IS_EMPTY);
        }
        return OssUtils.fileUpload(data, newFileName);
    }

    @Override
    public List<String> saveBase64Picture(String[] base64Array, String filePath, String module, String func) throws GlobalException {
        List<String> resultList = new ArrayList<String>();
        if (base64Array == null || base64Array.length == 0) {
            throw new GlobalException(FileMgmtExceptionEnum.FILE_IS_EMPTY);
        }
        for (String base64Data : base64Array) {
            if (base64Data.contains("http")) {
                resultList.add(base64Data);
                continue;
            }
            String[] d = base64Data.split("base64,");
            String dataPrix = ""; //格式结尾
            String data = "";     //数据
            if (d != null && d.length == 2) {
                dataPrix = d[0];
                data = d[1];
            } else {
                throw new GlobalException(FileMgmtExceptionEnum.FILE_FORMAT_IS_CORRECT);
            }
            String suffix = "";
            if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {// data:image/jpeg;base64,base64编码的jpeg图片数据
                suffix = ".jpeg";
            } else if ("data:image/jpg;".equalsIgnoreCase(dataPrix)) {// data:image/x-icon;base64,base64编码的icon图片数据
                suffix = ".jpg";
            } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {// data:image/png;base64,base64编码的png图片数据
                suffix = ".png";
            } else {
                throw new GlobalException(FileMgmtExceptionEnum.FILE_FORMAT_IS_CORRECT);
            }
            String fileName = UUID.randomUUID().toString() + suffix;
            byte[] bs = Base64Utils.decodeFromString(data);
            try {
                // 使用apache提供的工具类操作流
                FileUtils.writeByteArrayToFile(new File(filePath + module + "/" + func + "/" + fileName), bs);
            } catch (Exception ee) {
                throw new GlobalException(FileMgmtExceptionEnum.FILE_FORMAT_IS_CORRECT);
            }
            resultList.add(module + "/" + func + "/" + fileName);
        }

        return resultList;
    }

}
