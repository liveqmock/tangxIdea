package com.topaiebiz.file.mgmt.util;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
@Slf4j
public class OssUtils {

	private static String endpoint;

	private static String accessKeyId;

	private static String accessKeySecret;
	
	private static String bucket;

	private static String env;

	//OSSClient实例
	private static OSSClient ossClient;
	
	private OssUtils() {
	}
	
	//上传图片
	public static String fileUpload(byte[] file, String fileName) {
		if(ossClient == null){
			ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
		}
		log.info("上传到的bucket为：{}",bucket);
		// 上传
		ossClient.putObject(bucket, fileName, new ByteArrayInputStream(file));
		log.info("上传到fileName为：{}", fileName);
		return fileName;
	}
	
	//删除图片
	public static void deleteFile(String fileName) {
        if(ossClient == null){
            ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        }
		ossClient.deleteObject(bucket, fileName);
	}


	protected static void setEndpoint(String endpoint){
		OssUtils.endpoint = endpoint;
	}

	protected static void setAccessKeyId(String accessKeyId){
		OssUtils.accessKeyId = accessKeyId;
	}

	protected static void setAccessKeySecret(String accessKeySecret){
		OssUtils.accessKeySecret = accessKeySecret;
	}

	protected static void setBucket(String bucket){
		OssUtils.bucket = bucket;
	}

	protected static void setEnv(String env){
		OssUtils.env = env;
	}

	public static String getEnvDir(){
		if ("prod".equals(env) || "pre".equals(env)) {
			return "";
		}
		return "test/";
	}
}
