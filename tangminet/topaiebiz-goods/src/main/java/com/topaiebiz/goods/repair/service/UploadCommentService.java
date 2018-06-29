package com.topaiebiz.goods.repair.service;

import com.nebulapaas.web.response.ResponseInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:24 2018/4/4
 * @Modified by:
 */
public interface UploadCommentService {

	/**
	 * Description 评价数据批量上传
	 * <p>
	 * Author Hedda
	 *
	 * @param multipartFile    上传的文件
	 *
	 * @return
	 */
	ResponseInfo uploadSkuComment(MultipartFile multipartFile) throws IOException, Exception;
}
