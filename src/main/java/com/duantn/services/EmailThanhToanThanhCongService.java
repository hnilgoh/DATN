package com.duantn.services;

import com.duantn.entities.KhoaHoc;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.lowagie.text.pdf.BaseFont;

@Service
public class EmailThanhToanThanhCongService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendPaymentSuccessEmail(String toEmail, String tenNguoiDung, String giaoDichId, String tongTien,
            List<KhoaHoc> danhSachKhoaHoc) {
        try {
            // Tạo email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(toEmail);
            helper.setSubject("Xác nhận thanh toán thành công");

            // Tạo nội dung HTML bằng Thymeleaf
            Context context = new Context();
            context.setVariable("tenNguoiDung", tenNguoiDung);
            context.setVariable("giaoDichId", giaoDichId);
            context.setVariable("tongTien", tongTien);
            String html = templateEngine.process("views/gdienHocVien/email-thanh-toan-thanh-cong.html", context);
            helper.setText(html, true);

            // Tạo PDF hóa đơn
            ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, pdfOutput);
            document.open();

            // Font hỗ trợ tiếng Việt có dấu
            String fontPath = getClass()
                    .getClassLoader()
                    .getResource("fonts/DejaVuSans.ttf")
                    .getPath();

            // Giải mã URL nếu có khoảng trắng hoặc ký tự đặc biệt
            fontPath = java.net.URLDecoder.decode(fontPath, java.nio.charset.StandardCharsets.UTF_8);

            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 16, Font.BOLD);
            Font normalFont = new Font(bf, 12, Font.NORMAL);
            Font boldFont = new Font(bf, 12, Font.BOLD);

            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("👤 Họ tên học viên: " + tenNguoiDung, normalFont));
            document.add(new Paragraph("🧾 Mã giao dịch: " + giaoDichId, normalFont));
            document.add(new Paragraph("💰 Tổng tiền: " + tongTien + " ₫", normalFont));
            document.add(Chunk.NEWLINE);

            // Bảng khóa học
            PdfPTable table = new PdfPTable(new float[] { 3, 5, 2 });
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            table.addCell(new Phrase("Tên khóa học", boldFont));
            table.addCell(new Phrase("Mô tả", boldFont));
            table.addCell(new Phrase("Đơn giá", boldFont));

            for (KhoaHoc kh : danhSachKhoaHoc) {
                table.addCell(new Phrase(kh.getTenKhoaHoc(), normalFont));
                table.addCell(new Phrase(kh.getMoTa(), normalFont));
                table.addCell(new Phrase(kh.getGiaHienTai().toPlainString() + " ₫", normalFont));
            }

            document.add(table);
            document.close();

            // Đính kèm file PDF
            ByteArrayResource pdfAttachment = new ByteArrayResource(pdfOutput.toByteArray());
            String fileName = "Hoa_Don_GLOBALEDU_" + giaoDichId + ".pdf";
            helper.addAttachment(fileName, pdfAttachment);

            // Gửi email
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
