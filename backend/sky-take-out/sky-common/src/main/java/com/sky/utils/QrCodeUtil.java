package com.sky.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具
 */
public class QrCodeUtil {

    private static final int QR_CODE_WIDTH = 400;
    private static final int QR_CODE_HEIGHT = 400;
    private static final int TITLE_HEIGHT = 30;

    /**
     * 生成带标题的二维码图片（字节数组）
     */
    public static byte[] generateQRCodeWithTitle(String content, String title) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

        BufferedImage image = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT + TITLE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = image.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, QR_CODE_WIDTH, QR_CODE_HEIGHT + TITLE_HEIGHT);
        gs.setColor(Color.BLACK);

        // 绘制二维码
        for (int x = 0; x < bitMatrix.getWidth(); x++) {
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                if (bitMatrix.get(x, y)) {
                    gs.fillRect(x, y + TITLE_HEIGHT, 1, 1);
                }
            }
        }

        // 绘制标题
        gs.setFont(new Font("微软雅黑", Font.BOLD, 16));
        gs.drawString(title, 10, TITLE_HEIGHT - 8);
        gs.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * 生成纯二维码（字节数组）
     */
    public static byte[] generateQRCode(String content) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

        BufferedImage image = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = image.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, QR_CODE_WIDTH, QR_CODE_HEIGHT);
        gs.setColor(Color.BLACK);

        for (int x = 0; x < bitMatrix.getWidth(); x++) {
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                if (bitMatrix.get(x, y)) {
                    gs.fillRect(x, y, 1, 1);
                }
            }
        }
        gs.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}
