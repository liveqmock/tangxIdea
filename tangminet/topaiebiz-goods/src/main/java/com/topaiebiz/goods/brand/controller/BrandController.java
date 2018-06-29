package com.topaiebiz.goods.brand.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.api.BrandApi;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.brand.dto.BrandQueryDto;
import com.topaiebiz.goods.brand.service.BrandService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Description 商品品牌控制层
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年8月23日 下午4:14:40
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/brand", method = RequestMethod.POST)
public class BrandController {

  @Autowired
  private BrandService brandService;

  /**
   * Description 商品品牌添加
   * <p>
   * <p>Author Hedda
   *
   * @param brandDto 商品品牌dto
   * @param result   错误结果
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌添加")
  @RequestMapping(path = "/addBrand")
  public ResponseInfo addBrand(@RequestBody @Valid BrandDto brandDto, BindingResult result)
          throws GlobalException {
    /** 对商品品牌字段进行校验 */
    if (result.hasErrors()) {
      /** 初始化非法参数的提示信息。 */
      IllegalParamValidationUtils.initIllegalParamMsg(result);
      /** 获取非法参数异常信息对象，并抛出异常。 */
      throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
    }
    /** 对商品品牌进行添加 */
    return new ResponseInfo(brandService.saveBrand(brandDto));
  }

  /**
   * Description 商品品牌修改
   * <p>
   * <p>Author Hedda
   *
   * @param brandDto 商品品牌dto
   * @param result   错误结果
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌修改")
  @RequestMapping(path = "/editBrand")
  public ResponseInfo editbrand(@RequestBody @Valid BrandDto brandDto, BindingResult result)
          throws GlobalException {
    /** 对商品品牌字段进行校验 */
    if (result.hasErrors()) {
      /** 初始化非法参数的提示信息。 */
      IllegalParamValidationUtils.initIllegalParamMsg(result);
      /** 获取非法参数异常信息对象，并抛出异常。 */
      throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
    }
    return new ResponseInfo(brandService.modifyBrand(brandDto));
  }

  /**
   * Description 批量逻辑删除商品品牌
   * <p>
   * <p>Author Hedda
   *
   * @param id 商品品牌id
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌删除")
  @RequestMapping(path = "/cancelBrands")
  public ResponseInfo cancelBrands(@RequestBody Long[] id) throws GlobalException {
    return new ResponseInfo(brandService.removeBrands(id));
  }

  /**
   * Description 根据id查询品牌信息
   * <p>
   * <p>Author Hedda
   *
   * @param id 商品品牌ID
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌回显")
  @RequestMapping(path = "/findBrandById/{id}")
  public ResponseInfo findbrandById(@PathVariable Long id) throws GlobalException {
    BrandDto brand = brandService.findBrandById(id);
    return new ResponseInfo(brand);
  }

  /**
   * Description 商品品牌分页检索
   * <p>
   * <p>Author Hedda
   *
   * @param brandDto 商品品牌dto
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌列表分页检索")
  @RequestMapping(path = "/getBrandList")
  public ResponseInfo getBrandList(@RequestBody BrandQueryDto brandDto) throws GlobalException {
    PageInfo<BrandDto> list = brandService.getBrandList(brandDto);
    return new ResponseInfo(list);
  }


  /**
   * Description 品牌名称实时查询
   * <p>
   * <p>Author Hedda
   *
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "品牌名称实时查询")
  @RequestMapping(path = "/queryName")
  public ResponseInfo queryName(@RequestBody BrandQueryDto brandQueryDto) throws GlobalException {
    List<String> list = brandService.queryName(brandQueryDto.getName());
    return new ResponseInfo(list);
  }

  /**
   * Description 查看商品品牌列表
   * <p>
   * <p>Author Hedda
   *
   * @return ResponseInfo
   * @throws GlobalException
   */
  @PermissionController(value = PermitType.PLATFORM, operationName = "商品品牌列表")
  @RequestMapping(value = "/listBrand")
  public ResponseInfo listBrand() throws GlobalException {
    List<BrandDto> listbrand = brandService.getBrands();
    return new ResponseInfo(listbrand);
  }

  @Autowired
  private BrandApi brandApi;

  @RequestMapping(value = "/list")
  public ResponseInfo list() throws GlobalException {

    return new ResponseInfo(brandApi.getBrand(1L));
  }

}
