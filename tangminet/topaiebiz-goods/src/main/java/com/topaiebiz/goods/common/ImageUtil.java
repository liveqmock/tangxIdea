package com.topaiebiz.goods.common;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by hecaifeng on 2018/5/10.
 */
@Slf4j
public class ImageUtil {

    private static String DEFAULT_PREVFIX = "thumb_";
    private static Boolean DEFAULT_FORCE = false;//建议该值为false


    /**
     * Description 图片裁剪
     * <p>
     * Author Hedda
     *
     * @param imagePath 原图片路径
     * @param w         缩略图宽
     * @param h         缩略图高
     * @param prevfix   生成缩略图的前缀
     * @param force     是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public static String thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
        File imgFile = new File(imagePath);
        String newImage = null;
        if (imgFile.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if (imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0) {
                    log.error("对不起，图像后缀是非法的。标准图像后缀是{}" + types);
                    return null;
                }
                log.warn("需要生成的图片为： width:{}, height:{}.", w, h);
                Image img = ImageIO.read(imgFile);
                if (!force) {
                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if (width != w || height != h) {
                        h = Integer.parseInt(new java.text.DecimalFormat("0").format(h));
                        w = Integer.parseInt(new java.text.DecimalFormat("0").format(w));
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                String p = imgFile.getPath();
                // 将图片保存在原目录并加上前缀
                ImageIO.write(bi, suffix, new File(p.substring(0, p.lastIndexOf(File.separator)) + File.separator + prevfix + imgFile.getName()));
                File file = new File(p.substring(0, p.lastIndexOf(File.separator)) + File.separator + prevfix + imgFile.getName());
                newImage = file.getName();
                log.debug("缩略图在原路径下生成成功");
            } catch (IOException o) {
                log.error("裁剪图片失败！", o);
            }
        } else {
            log.warn("该图片不存在！");
        }
        return newImage;
    }

    /*public static void main(String[] args) {
        new ImageUtil().thumbnailImage("http://oss.motherbuy.com/anonymity/e6a3c26a-19b7-4ade-b9be-26f25ece057a.jpg", 100, 150, DEFAULT_PREVFIX, DEFAULT_FORCE);
    }*/

}
