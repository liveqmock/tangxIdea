package com.topaiebiz.file.mgmt.api;

import com.topaiebiz.file.api.FileUploadApi;
import com.topaiebiz.file.mgmt.service.FileMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileUploadApiImpl implements FileUploadApi {

    @Autowired
    private FileMgmtService fileMgmtService;

    @Override
    public String uploadFile(byte[] data, String newFileName) {
        return fileMgmtService.uploadFile(data, newFileName);
    }
}
