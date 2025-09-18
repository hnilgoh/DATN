package com.duantn.controllers.controllerHocVien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/studend")
public class ChiaSeKhoaHocController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @PostMapping("/share-course")
    @ResponseBody
    public ResponseEntity<String> shareCourseByEmail(
            @RequestParam("courseId") Integer courseId,
            @RequestParam("recipientEmail") String recipientEmail,
            Authentication authentication,
            HttpServletRequest request) {

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập để chia sẻ.");
        }

        try {
            TaiKhoan sender = taiKhoanRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi."));

            KhoaHoc course = khoaHocRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học."));

            sendShareEmail(sender.getName(), recipientEmail, course, request);

            return ResponseEntity.ok("✅ Khóa học đã được chia sẻ thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Gửi email thất bại: " + e.getMessage());
        }
    }

    private void sendShareEmail(String senderName, String recipientEmail, KhoaHoc course,
            HttpServletRequest request) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(senderName + " đã chia sẻ một khóa học thú vị với bạn!");
        helper.setFrom("globaledu237@gmail.com", "GlobalEdu");

        String courseUrl = request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + "/khoaHoc/" + course.getKhoahocId();

        String htmlContent = String.format(
                """
                        <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <h2 style="color: #0056b3;">Xin chào,</h2>
                            <p>Người bạn <strong>%s</strong> của bạn nghĩ rằng bạn sẽ thích khóa học này trên GlobalEdu:</p>

                            <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; margin: 20px 0;">
                                <h3 style="margin-top: 0;">%s</h3>
                                <p>%s</p>
                                <a href="%s" style="display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Xem chi tiết khóa học</a>
                            </div>

                            <p>Hãy khám phá và nâng cao kiến thức của bạn ngay hôm nay!</p>
                            <br>
                            <p>Trân trọng,<br><strong>Đội ngũ GlobalEdu</strong></p>
                        </div>
                        """,
                senderName, course.getTenKhoaHoc(), course.getMoTa(), courseUrl);

        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
