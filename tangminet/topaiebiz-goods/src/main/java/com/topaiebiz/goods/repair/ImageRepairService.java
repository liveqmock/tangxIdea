package com.topaiebiz.goods.repair;

import com.topaiebiz.goods.repair.dto.ItemPicDTO;
import com.topaiebiz.goods.repair.service.ItemImageRepairService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/***
 * @author yfeng
 * @date 2018-03-09 17:53
 */
@Service
@Slf4j
public class ImageRepairService implements InitializingBean {

    @Autowired
    private ItemImageRepairService itemImageRepairService;

    private ThreadPoolExecutor executor;

    private boolean dataLoaded = false;

    private Map<Long, List<ItemPicDTO>> imgDatas = new HashMap();
    private Set<String> imgNameSet = new HashSet<>();

    private String filePath = "D:\\数据迁移\\data.xlsx";

    public void repair(Long itemId){
        loadExcel();
        try {
            List<ItemPicDTO> pics = imgDatas.get(itemId);
            itemImageRepairService.repairItemImages(itemId, pics);
        } catch (Exception ex) {
            log.info("{} repair fail",itemId);
            log.error(ex.getMessage(), ex);
        }
    }

    @AllArgsConstructor
    class ItemPicTask implements Runnable {
        private Long itemId;
        private CountDownLatch cdl;

        @Override
        public void run() {
            List<ItemPicDTO> pics = imgDatas.get(itemId);
            try {
                itemImageRepairService.repairItemImages(itemId, pics);
            } catch (Exception ex) {
                log.info("{} repair fail",itemId);
                log.error(ex.getMessage(), ex);
            }
            cdl.countDown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(10 * 10000);
        executor = new ThreadPoolExecutor(8, 8, 1, TimeUnit.SECONDS, queue);
    }

    private void loadData(long itemId, long storeId, String imgName, int def) {
        if (imgNameSet.contains(imgName)) {
            log.info("{}重复", imgName);
            return;
        }
        String imgUrl = StringUtils.join("shop/store/goods/", storeId, "/", imgName);
        ItemPicDTO pic = new ItemPicDTO();
        pic.setItemId(itemId);
        pic.setImgUrl(imgUrl);
        pic.setIsDef(1 == def);
        pic.setStoreId(storeId);
        pic.setIsMain(false);
        List<ItemPicDTO> imgs = imgDatas.get(itemId);
        if (imgs == null) {
            imgs = new ArrayList<>();
            imgDatas.put(itemId, imgs);
        }
        imgs.add(pic);
        imgNameSet.add(imgName);
    }

    private void loadExcel() {
        if (dataLoaded) {
            return;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(OPCPackage.open(is));
            Sheet sheet = workbook.getSheetAt(0);
            int endRow = sheet.getLastRowNum();
            for (int i = 0; i <= endRow; i++) {
                Row row = sheet.getRow(i);
                long itemId = (long) row.getCell(1).getNumericCellValue();
                long storeId = (long) row.getCell(2).getNumericCellValue();
                String imgUrl = row.getCell(4).getStringCellValue();
                int defaultVal = (int) row.getCell(5).getNumericCellValue();
                loadData(itemId, storeId, imgUrl, defaultVal);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        dataLoaded = true;
    }


    public void start() {
        loadExcel();
        CountDownLatch cdl = new CountDownLatch(imgDatas.size());
        imgDatas.keySet().forEach(itemId -> {
            executor.submit(new ItemPicTask(itemId, cdl));
        });
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        log.info("任务完成!!! >>>>>>>>>");

    }
}