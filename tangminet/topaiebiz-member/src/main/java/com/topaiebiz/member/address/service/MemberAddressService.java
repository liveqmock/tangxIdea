package com.topaiebiz.member.address.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.address.entity.MemberAddressEntity;
import com.topaiebiz.member.dto.address.MemberAddressDto;

import java.util.List;

public interface MemberAddressService extends IService<MemberAddressEntity> {
    /**
     * Description：添加会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param entity 会员收货地址entity
     * @return
     */
    Boolean addMemberAddress(MemberAddressDto memberAddressDto);

    /**
     * Description：修改会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param memberAddressDto 会员收货地址entitDto
     * @return
     * @throws GlobalException
     */
    Boolean modifyMemberAddress(MemberAddressDto memberAddressDto);

    /**
     * Description： 根据id查询收货地址信息
     * <p>
     * Author Scott.Yang
     *
     * @param addressId
     * @return
     */
    MemberAddressDto findMemberAddress(Long memberId, Long addressId);

    /**
     * Description： 删除会员收货地址
     * <p>
     * Author Scott.Yang
     *
     * @param addressId
     */
    Boolean removeAddress(Long memberId, Long addressId);

    /**
     * Description： 会员收货地址列表
     * <p>
     * Author Scott.Yang
     *
     * @return
     */
    List<MemberAddressDto> getMemberAddressList(Long memberId);


    MemberAddressDto getDefaultAddress(Long memberId);

    Boolean setDefaultAddress(Long memberId, Long addressId);
}
