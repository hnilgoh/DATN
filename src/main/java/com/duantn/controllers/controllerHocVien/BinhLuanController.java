package com.duantn.controllers.controllerHocVien;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BinhLuan;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.AuthService;
import com.duantn.services.BaiGiangService;
import com.duantn.services.BinhLuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bai-giang/{baiGiangId}/binh-luan")
public class BinhLuanController {

    @Autowired
    private BinhLuanService binhLuanService;

    @Autowired
    private BaiGiangService baiGiangService;

    @Autowired
    private AuthService authService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private TaiKhoan getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authService.getTaiKhoanFromAuth(authentication);
    }

    // View page
    @GetMapping
    public String viewBinhLuan(@PathVariable("baiGiangId") Integer baiGiangId, Model model) {
        BaiGiang baiGiang = baiGiangService.findBaiGiangById(baiGiangId);
        if (baiGiang == null)
            return "redirect:/khoa-hoc?error=notfound";

        List<BinhLuan> rootComments = binhLuanService.getCommentsByBaiGiangId(baiGiangId);
        List<BinhLuan> allComments = binhLuanService.getAllCommentsByBaiGiangId(baiGiangId);
        Map<Integer, List<BinhLuan>> childrenMap = allComments.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getBinhluanId()));

        TaiKhoan currentUser = getCurrentUser();

        model.addAttribute("baiGiang", baiGiang);
        model.addAttribute("rootComments", rootComments);
        model.addAttribute("childrenMap", childrenMap);
        model.addAttribute("loggedInEmail", currentUser != null ? currentUser.getEmail() : null);

        return "views/gdienHocVien/xem-khoa-hoc";
    }

    // Add comment or reply
    @PostMapping({ "/add", "/reply" })
    @ResponseBody
    public ResponseEntity<?> addOrReplyComment(@PathVariable("baiGiangId") Integer baiGiangId,
            @RequestParam String noiDung,
            @RequestParam(required = false) Integer parentId) {
        TaiKhoan user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập.");

        BinhLuan comment;
        if (parentId != null) {
            comment = binhLuanService.replyToComment(baiGiangId, parentId, noiDung, user.getTaikhoanId());
        } else {
            comment = binhLuanService.saveComment(baiGiangId, noiDung, user.getTaikhoanId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("binhluanId", comment.getBinhluanId());
        response.put("noiDung", comment.getNoiDung());
        response.put("taikhoanName", comment.getTaikhoan() != null ? comment.getTaikhoan().getName() : "Ẩn danh");
        response.put("taikhoanAvatar",
                comment.getTaikhoan() != null ? comment.getTaikhoan().getAvatar() : "/images/default-avatar.png");
        response.put("ngayBinhLuan", comment.getNgayBinhLuan().format(FORMATTER));
        response.put("parentId", parentId);

        return ResponseEntity.ok(response);
    }

    // Delete comment
    @DeleteMapping("/delete/{binhluanId}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable("binhluanId") Integer binhluanId) {
        TaiKhoan user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập.");

        try {
            boolean deleted = binhLuanService.deleteComment(binhluanId, user.getTaikhoanId());
            return deleted ? ResponseEntity.ok("Bình luận đã được xóa.")
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không thể xóa bình luận này.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xảy ra lỗi khi xóa bình luận (có thể có reply con).");
        }
    }

    // Get replies
    @GetMapping("/replies/{parentId}")
    @ResponseBody
    public List<BinhLuan> getReplies(@PathVariable Integer parentId) {
        return binhLuanService.getRepliesByParentCommentId(parentId);
    }
}