package com.topaiebiz.decorate.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.DateUtils;
import com.nebulapaas.common.ExportUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.ComponentBuilder;
import com.topaiebiz.decorate.component.ItemExcelVO;
import com.topaiebiz.decorate.component.ItemListVO;
import com.topaiebiz.decorate.component.ItemVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.constant.ErrorInfo;
import com.topaiebiz.decorate.dao.ComponentItemDao;
import com.topaiebiz.decorate.dao.ContentDao;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.ExportItemDto;
import com.topaiebiz.decorate.dto.ItemExcelDto;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import com.topaiebiz.decorate.entity.ComponentItemEntity;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.decorate.service.CTerminalService;
import com.topaiebiz.decorate.service.ContentService;
import com.topaiebiz.decorate.util.ExcelImportUtil;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDecorateDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Slf4j
public class ContentServiceImpl extends ServiceImpl<ContentDao, ComponentContentEntity> implements ContentService {

    @Autowired
    private CTerminalService cTerminalService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ComponentBuilder componentBuilder;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private ComponentItemDao componentItemDao;


    private FormulaEvaluator evaluator;

    @Override
    @Transactional
    public Long create(ComponentContentDto componentContentDto) {
        if (null == componentContentDto.getType()) {
            throw new GlobalException(DecorateExcepionEnum.COMPONENT_TYPE_NOT_NULL);
        }

        if (null == componentContentDto.getComponentId()) {
            throw new GlobalException(DecorateExcepionEnum.COMPONENT_ID_NOT_NULL);
        }
        componentBuilder.buildComponent(componentContentDto);//校验组件内容
        ComponentContentEntity entity = new ComponentContentEntity();
        BeanUtils.copyProperties(componentContentDto, entity);
        contentDao.insert(entity);
        componentBuilder.dealItem(componentContentDto);
        return entity.getId();
    }

    @Override
    @Transactional
    public Long modify(ComponentContentDto componentContentDto) {
        if (null == componentContentDto.getId()) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //逻辑删除
        ComponentContentEntity entity = new ComponentContentEntity();
        entity.cleanInit();
        entity.setId(componentContentDto.getId());
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        entity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        entity.setLastModifiedTime(new Date());
        contentDao.updateById(entity);
        //新增
        return create(componentContentDto);
    }

    @Override
    public ComponentContentDto preview(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        return componentBuilder.loadDataFromDB(id);
    }

    @Override
    public List<ItemExcelDto> importItem(MultipartFile file) {
        String fileName = file.getOriginalFilename();//获取文件名称
        log.info(" excel文件名称为：" + fileName);

        //校验excel文件
        if (false == ExcelImportUtil.validateExcel(fileName)) {
            throw new GlobalException(DecorateExcepionEnum.EXCEL_FORMAT_ERROR);
        }

        //将通过SDK查询到商品详情数据放入该list中
        List<GoodsDecorateDTO> goodsDecorateDTOS = new ArrayList<>();

        //return
        List<ItemExcelDto> itemExcelDtos = new ArrayList<>();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            //获取有多少sheet
            int numberOfSheets = workbook.getNumberOfSheets();
            //将所有的单元格数据放入该list中,作为参数
            List<GoodsDecorateDTO> params = new ArrayList<>();

            //遍历sheet
            for (int i = 0; i < numberOfSheets; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                //获取sheet中一共有多少行
                int numberOfRows = sheet.getPhysicalNumberOfRows();
                log.info("第" + (i + 1) + "个sheet的行数为:" + numberOfRows);

                //如果只有一行,为标题行
                if (1 == numberOfRows) {
                    continue;
                }

                for (int j = 1; j < numberOfRows; j++) {
                    HSSFRow row = sheet.getRow(j);
                    ItemExcelVO itemExcelVO = new ItemExcelVO();
                    itemExcelVO.setSortNo(Long.valueOf(getCellValueByCell(row.getCell(0))));//排序值
                    itemExcelVO.setGoodsId(Long.valueOf(getCellValueByCell(row.getCell(1))));//商品id
                    //itemExcelVO.setItemName(getCellValueByCell(row.getCell(2)));//商品名称
                    //如果数据库不存在该商品终止导入
                    if (null == getItem(itemExcelVO.getGoodsId())) {
                        throw new GlobalException(new ErrorInfo("13000020", "item not exist",
                                itemExcelVO.getGoodsId()));
                    }
                    GoodsDecorateDTO goodsDecorateDTO = new GoodsDecorateDTO();
                    BeanUtils.copyProperties(itemExcelVO, goodsDecorateDTO);
                    params.add(goodsDecorateDTO);
                }
            }

            //获取所有的商品详情
            if (CollectionUtils.isNotEmpty(params)) {
                goodsDecorateDTOS = goodsApi.getGoodsDecorate(params);
                for (GoodsDecorateDTO goodsDecorateDTO : goodsDecorateDTOS) {
                    ItemExcelDto itemExcelDto = new ItemExcelDto();
                    BeanUtils.copyProperties(goodsDecorateDTO, itemExcelDto);
                    itemExcelDtos.add(itemExcelDto);
                }
            }

        } catch (IOException e) {
            log.info("excel文件转输入流失败:" + e.getMessage());
        }
        return itemExcelDtos;
    }

    @Override
    public void export(HttpServletResponse response, ExportItemDto exportItemDto) {

        List<ItemExcelDto> exportList = exportItemDto.getItemExcelDtos();
        String excelHeadColumn = ExportUtil.buildExcelHeadColumn("序号,商品id,商品名称");
        String excelBodyColumn;
        try {
            excelBodyColumn = ExportUtil.buildExcelBodyColumn(exportList, ItemExcelDto.class);
            ExportUtil.setRespProperties(StringUtils.join("商品导出"), response);
            ExportUtil.doExport(excelHeadColumn, excelBodyColumn, response.getOutputStream());
        } catch (Exception ex) {
            log.info(StringUtils.join("商品导出失败!!!!!!", ex.getMessage(), ex));
        }
    }


    @Override
    @JedisOperation
    public void removeItem(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        EntityWrapper<ComponentItemEntity> componentItemCondition = new EntityWrapper<>();
        componentItemCondition.eq("itemId", itemId);
        componentItemCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ComponentItemEntity> componentItemEntities = componentItemDao.selectList(componentItemCondition);
        ComponentItemEntity modifyComponentItemEntity = new ComponentItemEntity();
        modifyComponentItemEntity.cleanInit();
        modifyComponentItemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        modifyComponentItemEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        modifyComponentItemEntity.setLastModifiedTime(new Date());
        componentItemDao.update(modifyComponentItemEntity, componentItemCondition);
        if (CollectionUtils.isNotEmpty(componentItemEntities)) {
            List<Long> componentIdArr = getComponentIds(componentItemEntities);
            Map<Long, String> contentMap = getContentMap(componentIdArr);
            for (Long componentId : componentIdArr) {
                removeItemId(componentId, contentMap, itemId);
            }
        }
    }

    public List<Long> getComponentIds(List<ComponentItemEntity> componentItemEntities) {
        List<Long> componentIdArr = new ArrayList<>();
        Set<Long> componentIdSet = new HashSet<>();
        for (ComponentItemEntity componentItemEntity : componentItemEntities) {
            componentIdSet.add(componentItemEntity.getComponentId());
        }
        componentIdArr = Arrays.asList(componentIdSet.toArray(new Long[]{}));
        return componentIdArr;
    }

    @Override
    @JedisOperation
    public void editItem(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //查询componentId集合
        EntityWrapper<ComponentItemEntity> componentItemCondition = new EntityWrapper<>();
        componentItemCondition.eq("itemId", itemId);
        componentItemCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ComponentItemEntity> entities = componentItemDao.selectList(componentItemCondition);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<Long> componentIdArr = getComponentIds(entities);
            for (Long componentId : componentIdArr) {
                cTerminalService.loadContentCache(componentId);
            }
        }
    }

    @Override
    public void activeItem(List<Long> itemId) {
        if (CollectionUtils.isEmpty(itemId)) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //先查询该活动商品是否存在组件中
        EntityWrapper<ComponentItemEntity> itemCondition = new EntityWrapper<>();
        itemCondition.in("itemId", itemId);
        itemCondition.eq("deteledFlag", Constants.DeletedFlag.DELETED_NO);
        List<ComponentItemEntity> componentItemEntities = componentItemDao.selectList(itemCondition);
        if (CollectionUtils.isNotEmpty(componentItemEntities)) {
            List<Long> componentIds = getComponentIds(componentItemEntities);
            for (Long componentId : componentIds) {
                cTerminalService.loadContentCache(componentId);
            }
        }
    }

    @JedisOperation
    public void removeItemId(Long componentId, Map<Long, String> contentMap, Long itemId) {
        Jedis jedis = JedisContext.getJedis();
        String key = Constant.COMPONENT_KEY_PREFIX + componentId;
        ItemListVO itemListVO = JSON.parseObject(contentMap.get(componentId), ItemListVO.class);
        List<ItemVO> itemVOS = itemListVO.getItemVOS();
        Iterator<ItemVO> iterator = itemVOS.iterator();
        while (iterator.hasNext()) {
            ItemVO itemVO = iterator.next();
            if (itemId.equals(itemVO.getItemId())) {
                iterator.remove();
            }
        }
        EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
        contentCondition.eq("componentId", componentId);
        contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        ComponentContentEntity modifyContentEntity = new ComponentContentEntity();
        modifyContentEntity.cleanInit();
        modifyContentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        modifyContentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        modifyContentEntity.setLastModifiedTime(new Date());
        if (CollectionUtils.isEmpty(itemVOS)) {
            contentDao.update(modifyContentEntity, contentCondition);
            jedis.del(key);
        } else {
            //先更新表中的该条数据
            itemListVO.setItemVOS(itemVOS);
            modifyContentEntity.setContent(JSON.toJSONString(itemListVO));
            contentDao.update(modifyContentEntity, contentCondition);
            cTerminalService.loadContentCache(componentId);
        }
    }

    public Map getContentMap(List<Long> componentIds) {
        Map<Long, String> contentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(componentIds)) {
            EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
            contentCondition.in("componentId", componentIds);
            contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<ComponentContentEntity> contentEntities = contentDao.selectList(contentCondition);
            for (ComponentContentEntity contentEntity : contentEntities) {
                contentMap.put(contentEntity.getComponentId(), contentEntity.getContent());
            }
        }
        return contentMap;
    }

    public ItemDTO getItem(Long itemId) {
        ItemDTO itemDTO = goodsApi.getItem(itemId);
        return itemDTO == null ? null : itemDTO;
    }

    public String getCellValueByCell(HSSFCell cell) {
        if (null == cell || StringUtils.isEmpty(cell.toString().trim())) {
            return StringUtils.EMPTY;
        }
        String cellValue;
        int cellType = cell.getCellType();
        //表达式类型
        if (Cell.CELL_TYPE_FORMULA == cellType) {
            cellType = evaluator.evaluate(cell).getCellType();
        }
        switch (cellType) {
            case Cell.CELL_TYPE_STRING://字符串类型
                cellValue = cell.getStringCellValue().trim();
                cellValue = StringUtils.isEmpty(cellValue) ? StringUtils.EMPTY : cellValue;
                break;
            case Cell.CELL_TYPE_BOOLEAN://布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC://数值类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {//判断日期类型
                    cellValue = DateUtils.parseDateToString(cell.getDateCellValue(), "yyyy-MM-dd");
                } else {
                    cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                }
                break;
            default://其他类型
                cellValue = StringUtils.EMPTY;
                break;
        }
        return cellValue;
    }

}
