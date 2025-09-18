package com.duantn.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonPDFGenerator {

    public static byte[] taoHoaDonPDF(String tenHocVien, String giaoDichId, String tongTien,
            List<String> danhSachKhoaHoc) throws Exception {

        Document document = new Document(PageSize.A4, 36, 36, 36, 36); // Có margin
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Font hỗ trợ tiếng Việt
        BaseFont bf = BaseFont.createFont("src/main/resources/fonts/DejaVuSans.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontTieuDe = new Font(bf, 18, Font.BOLD, BaseColor.BLACK);
        Font fontChu = new Font(bf, 12, Font.NORMAL, BaseColor.DARK_GRAY);

        // Tiêu đề
        Paragraph tieuDe = new Paragraph("HÓA ĐƠN THANH TOÁN - GLOBALEDU", fontTieuDe);
        tieuDe.setAlignment(Element.ALIGN_CENTER);
        document.add(tieuDe);
        document.add(new Paragraph(" ")); // khoảng trắng

        // Thông tin học viên và giao dịch
        document.add(new Paragraph("Tên học viên: " + tenHocVien, fontChu));
        document.add(new Paragraph("Mã giao dịch: " + giaoDichId, fontChu));
        document.add(new Paragraph("Ngày tạo: " + LocalDateTime.now().toString(), fontChu));
        document.add(new Paragraph(" "));

        // Bảng khóa học
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3f, 2f });

        // Tiêu đề cột
        PdfPCell cell1 = new PdfPCell(new Phrase("Khóa học", fontChu));
        PdfPCell cell2 = new PdfPCell(new Phrase("Giá (VNĐ)", fontChu));
        cell1.setBackgroundColor(new BaseColor(230, 230, 250));
        cell2.setBackgroundColor(new BaseColor(230, 230, 250));
        table.addCell(cell1);
        table.addCell(cell2);

        for (String khoaHoc : danhSachKhoaHoc) {
            String[] parts = khoaHoc.split(";");
            String tenKhoaHoc = parts[0];
            String gia = parts.length > 1 ? parts[1] : "0";

            table.addCell(new Phrase(tenKhoaHoc, fontChu));
            table.addCell(new Phrase(gia + " ₫", fontChu));
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Tổng tiền: " + tongTien + " ₫", fontChu));
        document.add(new Paragraph("Cảm ơn bạn đã đăng ký học tại GLOBALEDU!", fontChu));

        document.close();
        writer.close();

        return baos.toByteArray();
    }
}
