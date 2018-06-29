package com.topaiebiz.merchant.store.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.file.mgmt.service.FileMgmtService;
import com.topaiebiz.merchant.enter.dao.MerchantAccountDao;
import com.topaiebiz.merchant.enter.dao.MerchantQualificationDao;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.info.dao.MerchantInfoDao;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.MerchantQualificationEntity;
import com.topaiebiz.merchant.info.entity.StoreInfoEntity;
import com.topaiebiz.merchant.store.service.MerchantRepairService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/***
 * @author yfeng
 * @date 2018-02-26 19:12
 */
@Slf4j
@Service
public class MerchantRepairServiceImpl implements MerchantRepairService {

    @Autowired
    private StoreInfoDao storeInfoDao;

    @Autowired
    private MerchantInfoDao merchantInfoDao;

    @Autowired
    private MerchantQualificationDao merchantQualificationDao;

    @Autowired
    private MerchantAccountDao merchantAccountDao;

    @Autowired
    private FileMgmtService fileMgmtService;

    @Override
    public boolean repairMerchants() {
        EntityWrapper<StoreInfoEntity> cond = new EntityWrapper<>();
        List<StoreInfoEntity> storeInfos = storeInfoDao.selectList(cond);
        for (StoreInfoEntity store : storeInfos) {
            repairStoreInfo(store);
        }
        EntityWrapper<MerchantQualificationEntity> qualificationcond = new EntityWrapper<>();
        List<MerchantQualificationEntity> merchantQualificationEntities = merchantQualificationDao.selectList(qualificationcond);
        for (MerchantQualificationEntity merchantQualificationEntity : merchantQualificationEntities) {
            repairMerchantQualificationInfo(merchantQualificationEntity);
        }
        EntityWrapper<MerchantAccountEntity> accountcond = new EntityWrapper<>();
        List<MerchantAccountEntity> merchantAccountEntities = merchantAccountDao.selectList(accountcond);
        for (MerchantAccountEntity merchantAccountEntity : merchantAccountEntities){
            repairmerchantAccountEntity(merchantAccountEntity);
        }
        return false;
    }

    @Override
    public void moteMerchantId() {
        EntityWrapper<StoreInfoEntity> cond = new EntityWrapper<>();
        List<StoreInfoEntity> storeInfos = storeInfoDao.selectList(cond);
        for (StoreInfoEntity store : storeInfos) {
            if (store.getMemberId()!=null){
                MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectMerchantAccountByMemberId(store.getMemberId());
                if(merchantAccountEntity !=null){
                    merchantAccountEntity.setMerchantId(store.getMerchantId());
                    merchantAccountDao.updateById(merchantAccountEntity);
                }
                MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectMerchantInfoByMemberId(store.getMemberId());
                if(merchantQualificationEntity !=null){
                    merchantQualificationEntity.setMerchantId(store.getMerchantId());
                    merchantQualificationDao.updateById(merchantQualificationEntity);
                }

            }

        }
    }

    private void repairStoreInfo(StoreInfoEntity store) {
        log.info("修复店铺头像{}",store.getId());
        updateStoreAvatar(store);
    }

    private void repairMerchantQualificationInfo(MerchantQualificationEntity merchantQualificationEntity) {
        log.info("修复资质图片{}",merchantQualificationEntity.getMerchantId());
        updateMerchantQualificationAvatar(merchantQualificationEntity);
    }
    private void repairmerchantAccountEntity(MerchantAccountEntity merchantAccountEntity) {
        log.info("修复银行账户开户行{}",merchantAccountEntity.getMerchantId());
        updateMerchantAccountEntityAvatar(merchantAccountEntity);
    }

    private void updateMerchantQualificationAvatar(MerchantQualificationEntity merchantQualificationEntity) {
        boolean update = false;
        //支付凭证
        if (StringUtils.isNotBlank(merchantQualificationEntity.getPayImage()) && !merchantQualificationEntity.getPayImage().startsWith("enter/")) {
            String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store_joinin/", merchantQualificationEntity.getPayImage());
            String newFileName = StringUtils.join("enter/", merchantQualificationEntity.getPayImage());
            try {
                byte[] datas = getImageContent(srcUrl);
                fileMgmtService.uploadFile(datas, newFileName);
                merchantQualificationEntity.setPayImage(newFileName);
                update = true;
            } catch (Exception ex) {
                log.error("PayImage srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
            }
        }

        //营业执照凭证
       if (StringUtils.isNotBlank(merchantQualificationEntity.getLicenseImage()) && !merchantQualificationEntity.getLicenseImage().startsWith("enter/")) {
            String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store_joinin/", merchantQualificationEntity.getLicenseImage());
            String fileExt = FilenameUtils.getExtension(merchantQualificationEntity.getLicenseImage());
            String newFileName = StringUtils.join("enter/", merchantQualificationEntity.getLicenseImage());
            try {
                byte[] datas = getImageContent(srcUrl);
                fileMgmtService.uploadFile(datas, newFileName);
                merchantQualificationEntity.setLicenseImage(newFileName);
                update = true;
            } catch (Exception ex) {
                log.error("LicenseImage srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
            }
        }
        //一般纳税人证明
        if (StringUtils.isNotBlank(merchantQualificationEntity.getTaxpayerImage()) && !merchantQualificationEntity.getTaxpayerImage().startsWith("enter/")) {
            String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store_joinin/", merchantQualificationEntity.getTaxpayerImage());
            String newFileName = StringUtils.join("enter/", merchantQualificationEntity.getTaxpayerImage());
            try {
                byte[] datas = getImageContent(srcUrl);
                fileMgmtService.uploadFile(datas, newFileName);
                merchantQualificationEntity.setTaxpayerImage(newFileName);
                update = true;
            } catch (Exception ex) {
                log.error("TaxpayerNoImage srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
            }
        }
        //税务登记证号电子版
       if (StringUtils.isNotBlank(merchantQualificationEntity.getTaxpayerNoImage()) && !merchantQualificationEntity.getTaxpayerNoImage().startsWith("enter/")) {
            String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store_joinin/", merchantQualificationEntity.getTaxpayerNoImage());
            String newFileName = StringUtils.join("enter/", merchantQualificationEntity.getTaxpayerNoImage());
            try {
                byte[] datas = getImageContent(srcUrl);
                fileMgmtService.uploadFile(datas, newFileName);
                merchantQualificationEntity.setTaxpayerNoImage(newFileName);
                update = true;
            } catch (Exception ex) {
                log.error("TaxpayerNoImage srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
            }
        }
        if (update) {
            merchantQualificationDao.updateById(merchantQualificationEntity);
        }


    }
    private void updateMerchantAccountEntityAvatar(MerchantAccountEntity merchantAccountEntity) {
        boolean update = false;
        //开户银行许可证电子版
        if (StringUtils.isNotBlank(merchantAccountEntity.getElectronicImage()) && !merchantAccountEntity.getElectronicImage().startsWith("enter/")) {
            String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store_joinin/", merchantAccountEntity.getElectronicImage());
            String newFileName = StringUtils.join("enter/", merchantAccountEntity.getElectronicImage());
            try {
                byte[] datas = getImageContent(srcUrl);
                fileMgmtService.uploadFile(datas, newFileName);
                merchantAccountEntity.setElectronicImage(newFileName);
                update = true;
            } catch (Exception ex) {
                //log.error(ex.getMessage(), ex);
                log.error("ElectronicImage srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
            }
        }
        if (update) {
            merchantAccountDao.updateById(merchantAccountEntity);
        }


    }

    private void updateStoreAvatar(StoreInfoEntity store) {
        if (StringUtils.isBlank(store.getImages()) || store.getImages().startsWith("shop/avatar/")) {
            return;
        }
        Long storeId = store.getId();
        String srcUrl = StringUtils.join("http://pic.motherbuy.com/shop/store/", store.getImages());
        String newFileName = StringUtils.join("shop/avatar/",store.getImages());
        try {
            byte[] data = getImageContent(srcUrl);
            fileMgmtService.uploadFile(data, newFileName);

            StoreInfoEntity update = new StoreInfoEntity();
            update.cleanInit();
            update.setImages(newFileName);
            update.setId(storeId);
            storeInfoDao.updateById(update);

            MerchantInfoEntity merchantUpdate = new MerchantInfoEntity();
            merchantUpdate.cleanInit();
            merchantUpdate.setImgages(newFileName);
            merchantUpdate.setId(storeId);
            merchantInfoDao.updateById(merchantUpdate);
        } catch (Exception ex) {
            log.error("updateStoreAvatar srcUrl {} 修复失败,errorMsg: {}", srcUrl,ex.getMessage());
        }
    }

    private static byte[] getImageContent(String srcUrl) throws Exception {
        int timeout = 3000;
        return Request.Get(srcUrl).connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asBytes();
    }
}