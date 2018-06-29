package com.topaiebiz.decorate.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.ExportItemDto;
import com.topaiebiz.decorate.dto.ItemExcelDto;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface ContentService extends IService<ComponentContentEntity> {

    /**
     * 添加组件内容
     *
     * @param componentContentDto
     */
    Long create(ComponentContentDto componentContentDto);

    /**
     * 修改组件内容
     *
     * @param componentContentDto
     */
    Long modify(ComponentContentDto componentContentDto);

    /**
     * 预览组件内容
     *
     * @param id
     * @return
     */
    ComponentContentDto preview(Long id);

    /**
     * 导入商品excel
     *
     * @param file
     */
    List<ItemExcelDto> importItem(MultipartFile file);

    /**
     * 导出
     *
     * @param exportItemDto
     */
    void export(HttpServletResponse response, ExportItemDto exportItemDto);

    /**
     * 删除某个商品
     *
     * @param itemId
     */
    void removeItem(Long itemId);

    /**
     * 编辑某个商品
     *
     * @param itemId
     */
    void editItem(Long itemId);

    /**
     * 活动某个商品
     *
     * @param itemId
     */
    void activeItem(List<Long> itemId);
}
