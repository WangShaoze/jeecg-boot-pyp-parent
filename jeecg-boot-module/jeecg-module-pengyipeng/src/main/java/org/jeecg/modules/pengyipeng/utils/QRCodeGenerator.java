package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: QRCodeGenerator
 * Package: org.jeecg.modules.pengyipeng.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 11:21
 * @Version: v1.0
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    // 默认配置
    private static final int DEFAULT_MARGIN = 1;
    private static final ErrorCorrectionLevel DEFAULT_ERROR_CORRECTION = ErrorCorrectionLevel.M;

    /**
     * 生成自定义样式的二维码
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param config 二维码样式配置
     * @return 二维码图片字节数组
     */
    public static byte[] generateCustomQRCode(
            String content,
            int width,
            int height,
            QRCodeConfig config) throws WriterException, IOException {

        // 设置二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, config.getErrorCorrectionLevel());
        hints.put(EncodeHintType.MARGIN, config.getMargin());

        // 生成二维码矩阵
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // 创建二维码图像并应用样式
        BufferedImage qrImage = createQRImage(bitMatrix, config);

        // 添加Logo（如果有）
        if (config.getLogoStream() != null) {
            qrImage = addLogoToQRCode(qrImage, config.getLogoStream(), config.getLogoSizeRatio());
        }

        // 转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 创建带样式的二维码图像
     */
    private static BufferedImage createQRImage(BitMatrix bitMatrix, QRCodeConfig config) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        // 使用 MatrixToImageWriter 默认绘制
        BufferedImage baseImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 再覆盖背景 / 前景色（支持自定义）
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(config.getBackgroundColor());
        graphics.fillRect(0, 0, width, height);

        // 遍历像素绘制
        graphics.setColor(config.getForegroundColor());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        graphics.dispose();
        return image;
    }


    /**
     * 给二维码添加Logo
     */
    private static BufferedImage addLogoToQRCode(BufferedImage qrImage, InputStream logoStream, float logoSizeRatio)
            throws IOException {
        // 读取Logo图片
        BufferedImage logoImage = ImageIO.read(logoStream);

        // 计算Logo尺寸（根据比例）
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int logoWidth = (int) (qrWidth * logoSizeRatio);
        int logoHeight = (int) (qrHeight * logoSizeRatio);

        // 确保Logo不会太大
        logoWidth = Math.min(logoWidth, qrWidth / 4);
        logoHeight = Math.min(logoHeight, qrHeight / 4);

        // 计算Logo位置（居中）
        int x = (qrWidth - logoWidth) / 2;
        int y = (qrHeight - logoHeight) / 2;

        // 绘制Logo
        Graphics2D graphics = qrImage.createGraphics();
        graphics.drawImage(logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH), x, y, null);

        // 可选：给Logo添加边框
        graphics.setStroke(new BasicStroke(2));
        graphics.setColor(Color.WHITE);
        graphics.drawRect(x, y, logoWidth, logoHeight);

        graphics.dispose();
        return qrImage;
    }

    /**
     * 二维码样式配置类
     */
    public static class QRCodeConfig {
        private Color foregroundColor = Color.BLACK; // 前景色（二维码颜色）
        private Color backgroundColor = Color.WHITE; // 背景色
        private int margin = DEFAULT_MARGIN; // 边距
        private ErrorCorrectionLevel errorCorrectionLevel = DEFAULT_ERROR_CORRECTION; // 纠错级别
        private boolean roundedCorners = false; // 是否圆角
        private InputStream logoStream = null; // Logo输入流
        private float logoSizeRatio = 0.2f; // Logo大小比例（占二维码的比例）

        // Getters and Setters
        public Color getForegroundColor() { return foregroundColor; }
        public void setForegroundColor(Color foregroundColor) { this.foregroundColor = foregroundColor; }
        public Color getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
        public int getMargin() { return margin; }
        public void setMargin(int margin) { this.margin = margin; }
        public ErrorCorrectionLevel getErrorCorrectionLevel() { return errorCorrectionLevel; }
        public void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) { this.errorCorrectionLevel = errorCorrectionLevel; }
        public boolean isRoundedCorners() { return roundedCorners; }
        public void setRoundedCorners(boolean roundedCorners) { this.roundedCorners = roundedCorners; }
        public InputStream getLogoStream() { return logoStream; }
        public void setLogoStream(InputStream logoStream) { this.logoStream = logoStream; }
        public float getLogoSizeRatio() { return logoSizeRatio; }
        public void setLogoSizeRatio(float logoSizeRatio) { this.logoSizeRatio = logoSizeRatio; }
    }
}
