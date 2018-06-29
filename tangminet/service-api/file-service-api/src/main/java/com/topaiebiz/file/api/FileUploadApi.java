package com.topaiebiz.file.api;

/**
 * 图片上传到OSS
 */
public interface FileUploadApi {

    //上传文件
    String uploadFile(byte[] data, String newFileName);

}
