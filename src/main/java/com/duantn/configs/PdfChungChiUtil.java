package com.duantn.configs;

import com.duantn.entities.ChungChi;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class PdfChungChiUtil {

        public static byte[] taoChungChiPDF(ChungChi cc) {
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Document document = new Document(PageSize.A4, 0, 0, 0, 0); // Không có margin
                        PdfWriter writer = PdfWriter.getInstance(document, baos);
                        document.open();

                        // Font hỗ trợ tiếng Việt
                        String fontPath = "src/main/resources/fonts/DejaVuSans.ttf";
                        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                        Font fontTitle = new Font(baseFont, 19, Font.BOLD, new BaseColor(0, 51, 153)); // Xanh đậm
                        Font fontChungChi = new Font(baseFont, 16, Font.BOLD, BaseColor.BLACK);
                        Font fontNormal = new Font(baseFont, 14, Font.NORMAL, new BaseColor(0, 70, 140));
                        Font fontCamOn = new Font(baseFont, 12, Font.ITALIC, BaseColor.DARK_GRAY);

                        // Ảnh nền
                        String imagePath = "src/main/resources/static/photos/chungchi3.png";
                        Image image = Image.getInstance(imagePath);
                        image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                        image.setAbsolutePosition(0, 0); // full trang
                        document.add(image);

                        PdfContentByte canvas = writer.getDirectContent();
                        float pageWidth = PageSize.A4.getWidth();
                        float pageHeight = PageSize.A4.getHeight();
                        float xCenter = pageWidth / 2;

                        // Căn giữa dọc trang
                        float baseY = pageHeight / 2 + 100; // dịch lên một chút để cân đối
                        float lineSpacing = 32;

                        // Dòng 1: HỆ THỐNG HỌC TRỰC TUYẾN GLOBALEDU
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("HỆ THỐNG HỌC TRỰC TUYẾN GLOBALEDU", fontTitle),
                                        xCenter, baseY, 0);

                        // Dòng 2: CHỨNG CHỈ
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("CHỨNG CHỈ HOÀN THÀNH KHÓA HỌC", fontChungChi),
                                        xCenter, baseY - lineSpacing * 1.2f, 0);

                        // Dòng 3: Chúc mừng tên học viên
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("Chúc mừng " + cc.getTenHocVien(), fontNormal),
                                        xCenter, baseY - lineSpacing * 2.4f, 0);

                        // Dòng 4: Khóa học
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("Đã hoàn thành khóa học: " + cc.getTenKhoaHoc(), fontNormal),
                                        xCenter, baseY - lineSpacing * 3.6f, 0);

                        // Dòng 5: Ngày cấp
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("Ngày cấp: " + cc.getNgayCap()
                                                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                                        fontNormal),
                                        xCenter, baseY - lineSpacing * 4.8f, 0);

                        // Dòng 6: Cảm ơn
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("Cảm ơn bạn đã đồng hành cùng GLOBALEDU.", fontCamOn),
                                        xCenter, baseY - lineSpacing * 6.0f, 0);

                        // Dòng 7: Gửi lời chúc
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                        new Phrase("Chúc bạn gặt hái nhiều thành công trong tương lai.", fontCamOn),
                                        xCenter, baseY - lineSpacing * 6.8f, 0);

                        document.close();
                        return baos.toByteArray();

                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
}
