package com.topaiebiz.goods.repair.service.impl;

import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentDao;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import com.topaiebiz.goods.comment.exception.GoodsSkuCommentExceptionEnum;
import com.topaiebiz.goods.constants.CommentConstants;
import com.topaiebiz.goods.goodsenum.GoodsRedisKey;
import com.topaiebiz.goods.repair.exception.GoodsRuntimeException;
import com.topaiebiz.goods.repair.service.UploadCommentService;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.member.api.MemberApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author tangx.w @Description: @Date: Create in 15:26 2018/4/4 @Modified by:
 */
@Service
@Slf4j
public class UploadCommentServiceImpl implements UploadCommentService {

	@Autowired
	private GoodsSkuDao goodsSkuDao;

	@Autowired
	private ItemDao itemDao;

	@Autowired
	private MemberApi memberApi;

	@Autowired
	private GoodsSkuCommentDao goodsSkuCommentDao;

	@Autowired
	private RedisCache redisCache;

	@Override
	public ResponseInfo uploadSkuComment(MultipartFile file) throws Exception {
		String fileName = file.getOriginalFilename();
		String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (!CommentConstants.UpLoad.FILE_FORMAT_XLS.equalsIgnoreCase(fileFormat)
				&& !CommentConstants.UpLoad.FILE_FORMAT_XLSX.equalsIgnoreCase(fileFormat)) {
			throw new GlobalException(GoodsSkuCommentExceptionEnum.UPLOADSKUCOMMENT_FILEFORMAT_ERROR);
		}
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		ResponseInfo responseInfo = this.checkExcel(sheet);
		return responseInfo;
	}

	private ResponseInfo checkExcel(Sheet sheet) throws ParseException {
		List<GoodsSkuCommentEntity> goodsSkuCommentLsit = new ArrayList<>();
		for (int row = 1; row < sheet.getLastRowNum() + 1; row++) {
			GoodsSkuCommentEntity goodsSkuCommentEntity = new GoodsSkuCommentEntity();
			for (int column = 1; column < CommentConstants.UpLoad.LAST_NUM; column++) {
				Row rowDate = sheet.getRow(row);
				Cell cell = rowDate.getCell(column);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String data = cell.getStringCellValue();
				String msg = "第" + (row + 1) + "行" + "第" + (column + 1) + "列:";
				switch (column) {
					case 1:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "itemId不能为空");
						}
						ItemEntity itemEntity = itemDao.selectById(Long.valueOf(data));
						if (itemEntity == null) {
							throw new GoodsRuntimeException(msg + "itemId不存在！");
						}
						updateItemCount(Long.valueOf(data));
						goodsSkuCommentEntity.setItemId(Long.valueOf(data));
						break;

					case 2:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "skuId不能为空");
						}
						GoodsSkuEntity goodsSkuEntity = goodsSkuDao.selectGoodsSkuBySkuId(Long.valueOf(data));
						if (goodsSkuEntity == null) {
							throw new GoodsRuntimeException(msg + "skuId不存在！");
						}
						goodsSkuCommentEntity.setSkuId(Long.valueOf(data));
						break;

					case 3:
						if (!StringUtils.isBlank(data)) {
							goodsSkuCommentEntity.setSaleFieldValue(data);
						}
						break;

					case 4:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "memberName不能为空");
						}
						goodsSkuCommentEntity.setUserName(data);
						break;
					case 5:
						// 正则表达式校验值是不是1,2,3
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "评价类型不能为空");
						}
						String regex = "^[1-3]$";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为1,2,3！");
						}
						goodsSkuCommentEntity.setType(Integer.valueOf(data));
						break;
					case 6:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "商品评价星级不能为空");
						}
						regex = "^[1-5]$";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为1,2,3,4,5！");
						}
						goodsSkuCommentEntity.setGoodsLevel(Integer.valueOf(data));
						break;
					case 7:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "物流服务星级不能为空");
						}
						regex = "^[1-5]$";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为1,2,3,4,5！");
						}
						goodsSkuCommentEntity.setLogisticsLevel(Integer.valueOf(data));
						break;
					case 8:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "服务态度好评度不能为空");
						}
						regex = "^[1-5]$";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为1,2,3,4,5！");
						}
						goodsSkuCommentEntity.setServeLevel(data);
						break;
					case 9:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "商品好评度不能为空");
						}
						regex = "^[1-5]$";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为1,2,3,4,5！");
						}
						goodsSkuCommentEntity.setGoodsReputation(Integer.valueOf(data));
						break;
					case 10:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "是否包含图片不能为空！");
						}
						regex = "^[0-1]$";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(data);
						if (!matcher.matches()) {
							throw new GoodsRuntimeException(msg + "值只能为0,1！");
						}
						goodsSkuCommentEntity.setIsImage(Integer.valueOf(data));
						break;
					case 11:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "请填写评价内容！");
						}
						goodsSkuCommentEntity.setDescription(data);
						break;
					case 12:
						if (StringUtils.isBlank(data)) {
							throw new GoodsRuntimeException(msg + "评价创建时间不能为空");
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date now = new Date();
						Date date;
						date = sdf.parse(data);
						if (now.getTime() < date.getTime()) {
							throw new GoodsRuntimeException(msg + "时间在当前之前之后！");
						}
						goodsSkuCommentEntity.setCreatedTime(date);
						break;
				}
			}
			goodsSkuCommentLsit.add(goodsSkuCommentEntity);
		}
		for (GoodsSkuCommentEntity goodsSkuCommentEntity : goodsSkuCommentLsit) {
			redisCache.delete(GoodsRedisKey.GOODS_DETAILS_COMMENT + goodsSkuCommentEntity.getItemId());
			goodsSkuCommentDao.insert(goodsSkuCommentEntity);
		}
		return new ResponseInfo();
	}

	public void updateItemCount(Long itemId) {
		ItemEntity itemEntity = itemDao.selectById(itemId);
		itemEntity.setCommentCount(itemEntity.getCommentCount() + 1);
		itemDao.updateById(itemEntity);
	}
}
