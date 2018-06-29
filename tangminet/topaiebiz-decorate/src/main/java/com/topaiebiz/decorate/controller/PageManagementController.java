package com.topaiebiz.decorate.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.decorate.dto.PageDetailDto;
import com.topaiebiz.decorate.service.PageManagementService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 页面管理controller
 *
 * @author huzhenjia
 * @since 2018/3/26
 */

@RestController
@RequestMapping(value = "/decorate/pageManagement")
public class PageManagementController {

    @Autowired
    private PageManagementService pageManagementService;

    /**
     * 新增页面
     *
     *
     * @param pageDetailDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加页面")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseInfo newPage(@RequestBody PageDetailDto pageDetailDto) {
        return new ResponseInfo(pageManagementService.newPage(pageDetailDto));
    }

    /**
     * 修改或添加发布时间
     *
     * @param pageDetailDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改发布时间")
    @RequestMapping(value = "/modifyPublishTime", method = RequestMethod.POST)
    public ResponseInfo modifyPublishTime(@RequestBody PageDetailDto pageDetailDto) {
        pageManagementService.modifyPublishTime(pageDetailDto);
        return new ResponseInfo();
    }

    /**
     * 分页查询页面
     *
     * @param pageDetailDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "根据条件分页查询页面")
    @RequestMapping(value = "/pagingQuery", method = RequestMethod.POST)
    public ResponseInfo pagingQuery(@RequestBody PageDetailDto pageDetailDto) {
        return new ResponseInfo(pageManagementService.pagingQuery(pageDetailDto));
    }

    /**
     * 删除某个页面
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "删除某个页面")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseInfo remove(@RequestBody Long id) {
        pageManagementService.remove(id);
        return new ResponseInfo();
    }


    /**
     * 发布
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "发布")
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ResponseInfo publish(@RequestBody Long id) {
        pageManagementService.publish(id);
        return new ResponseInfo();
    }

    /**
     * 下线
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "下线")
    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    public ResponseInfo offline(@RequestBody Long id) {
        pageManagementService.offline(id);
        return new ResponseInfo();
    }

    /**
     * 复制某个页面数据
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "复制某个页面数据")
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResponseInfo copy(@RequestBody Long id) {
        pageManagementService.copy(id);
        return new ResponseInfo();
    }

    /**
     * 预览功能
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "预览")
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public ResponseInfo preview(@RequestBody Long id) {
        pageManagementService.preview(id);
        return new ResponseInfo();
    }
}
