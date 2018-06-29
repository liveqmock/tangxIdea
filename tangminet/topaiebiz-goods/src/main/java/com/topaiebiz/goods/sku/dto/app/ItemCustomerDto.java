package com.topaiebiz.goods.sku.dto.app;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * Created by dell on 2018/1/19.
 */
@Data
public class ItemCustomerDto extends PagePO implements Comparable<ItemCustomerDto>, Serializable{
    private static final long serialVersionUID = 1087902683312028596L;
    /** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
    private Long id;

    /** 商品名称(标题显示的名称)。 */
    private String name;

    /** 市场价。*/
    private Double marketPrice;

    /** 默认价格（页面刚打开的价格）。 */
    private Double defaultPrice;

    /**
     * 最低价
     */
    private Double minPrice;

    /** 累计销量。 */
    private Long salesVolume;

    /** 图片。*/
    private String pictureName;

    /** 所属店铺。 */
    private Long belongStore;

    /**
     * 所属店铺集合。
     */
    private List<Long> storeIds;

    /** 所属品牌。 */
    private Long belongBrand;

    /** 适用年龄段。 */
    private Long ageId;

    /** 所属前台类目。 */
    private Long frontendCategory;

    /** 后台类目id。*/
    private Long belongCategory;

    /** 所属后台类目。 */
    private List<Long> belongCategoryIds;

    /** 根据价格进行排序。1为正序，0为倒叙 */
    private Integer price;

    /** 根据销量进行排序。0为倒叙，1为正序*/
    private Integer sales;

    /** 积分比例。小数形式。*/
    private Double integralRatio;

    /** 价格左区间。*/
    private BigDecimal priceRangeLeft;

    /** 价格右区间。*/
    private BigDecimal priceRangeRigth;

    public int compareTo(ItemCustomerDto i) {
        if (i.salesVolume != null) {
            if (this.salesVolume > i.salesVolume) {
                return 1;
            } else if (this.salesVolume < i.salesVolume) {
                return -1;
            }
        }
        return 0;
    }





}
