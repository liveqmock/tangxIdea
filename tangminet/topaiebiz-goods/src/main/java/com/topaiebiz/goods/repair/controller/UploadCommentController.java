package com.topaiebiz.goods.repair.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.repair.service.UploadCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:23 2018/4/4
 * @Modified by:
 */

@RestController
@RequestMapping(value = "/goods/upload", method = RequestMethod.POST)
public class UploadCommentController {

	@Autowired
	private UploadCommentService uploadCommentService;

	/**
	 * Description 评价数据批量上传
	 * <p>s
	 * Author Hedda
	 *
	 * @param multipartFile 上传的文件
	 * @return
	 * @throws GlobalException
	 */
	@RequestMapping(path = "/uploadSkuComment")
	public ResponseInfo uploadSkuComment(@RequestParam("file") MultipartFile multipartFile) throws Exception {
		return uploadCommentService.uploadSkuComment(multipartFile);
	}
}
