package com.duantn.controllers.controllerGiangVien;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BinhLuan;
import com.duantn.entities.Chuong;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/binh-luan-giang-vien/{baiGiangId}/binh-luan")
public class BinhLuanGiangVienController {

    @Autowired
    private BinhLuanService binhLuanService;

    @Autowired
    private BaiGiangService baiGiangService;

    @Autowired
    private AuthService authService;

    // Lấy TaiKhoan hiện tại, hỗ trợ cả login thường & OAuth2
    private TaiKhoan getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authService.getTaiKhoanFromAuth(authentication);
    }

    @GetMapping
    public String binhluan(@PathVariable("baiGiangId") Integer baiGiangId, Model model) {
        BaiGiang baiGiang = baiGiangService.findBaiGiangById(baiGiangId);

        if (baiGiang == null) {
            return "redirect:/khoahoc?error=notfound";
        }

        List<BinhLuan> rootComments = binhLuanService.getCommentsByBaiGiangId(baiGiangId);
        List<BinhLuan> allComments = binhLuanService.getAllCommentsByBaiGiangId(baiGiangId);

        Map<Integer, List<BinhLuan>> childrenMap = allComments.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getBinhluanId()));

        TaiKhoan currentUser = getCurrentUser();
        String loggedInEmail = currentUser != null ? currentUser.getEmail() : null;

        // ✅ Check xem currentUser có phải tác giả bài giảng không
        boolean isOwner = Optional.ofNullable(baiGiang)
                .map(BaiGiang::getChuong)
                .map(Chuong::getKhoahoc)
                .map(KhoaHoc::getGiangVien)
                .map(GiangVien::getTaikhoan)
                .map(TaiKhoan::getTaikhoanId)
                .filter(id -> id.equals(currentUser.getTaikhoanId()))
                .isPresent();

        model.addAttribute("baiGiang", baiGiang);
        model.addAttribute("rootComments", rootComments);
        model.addAttribute("childrenMap", childrenMap);
        model.addAttribute("loggedInEmail", loggedInEmail);
        model.addAttribute("isOwner", isOwner); // ✅ Truyền thêm biến này qua view

        return "views/KhoaHoc/noi-dung-khoa-hoc-chi-tiet";
    }

    @GetMapping("/replies/{parentId}")
    @ResponseBody
    public List<BinhLuan> getReplies(@PathVariable("parentId") Integer parentId) {
        return binhLuanService.getRepliesByParentCommentId(parentId);
    }

    @PostMapping("/add")
    public String addComment(@PathVariable("baiGiangId") Integer baiGiangId,
            @RequestParam("noiDung") String noiDung,
            RedirectAttributes redirectAttributes) {
        TaiKhoan user = getCurrentUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để bình luận.");
            return "redirect:/khoa-hoc/bai-giang/" + baiGiangId;
        }

        binhLuanService.saveComment(baiGiangId, noiDung, user.getTaikhoanId());
        redirectAttributes.addFlashAttribute("successMessage", "Bình luận của bạn đã được đăng.");
        return "redirect:/noi-dung-khoa-hoc-chi-tiet/khoa-hoc/noi-dung-bai-giang/" + baiGiangId;
    }

    @PostMapping("/reply/{parentId}")
    @ResponseBody
    public ResponseEntity<?> replyComment(@PathVariable("baiGiangId") Integer baiGiangId,
            @PathVariable("parentId") Integer parentId,
            @RequestParam("noiDung") String noiDung) {
        TaiKhoan user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn cần đăng nhập để trả lời bình luận.");
        }

        BinhLuan reply = binhLuanService.replyToComment(baiGiangId, parentId, noiDung, user.getTaikhoanId());

        Map<String, Object> response = new HashMap<>();
        response.put("noiDung", reply.getNoiDung());
        response.put("taikhoanName", reply.getTaikhoan().getName());
        response.put("ngayBinhLuan", reply.getNgayBinhLuan().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{binhluanId}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable("baiGiangId") Integer baiGiangId,
            @PathVariable("binhluanId") Integer binhluanId) {
        TaiKhoan user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn cần đăng nhập để xóa bình luận.");
        }

        boolean deleted = binhLuanService.deleteComment(binhluanId, user.getTaikhoanId());
        if (deleted) {
            return ResponseEntity.ok().body("Bình luận đã được xóa.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không thể xóa bình luận này.");
        }
    }
}