package com.topaiebiz.file.mgmt.service;

import com.nebulapaas.web.exception.GlobalException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface FileMgmtService {

	List<String> saveBase64Picture(String[] base64Array,String filePath, String module, String func)  throws GlobalException;

	String uploadFile(MultipartFile file);

	Set<String> uploadFiles(List<MultipartFile> files);

	String uploadFile(byte[] data, String newFileName);
}
