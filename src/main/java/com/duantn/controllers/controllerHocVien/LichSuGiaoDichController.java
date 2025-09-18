// package com.duantn.controllers.controllerHocVien;

// import com.duantn.entities.GiaoDichKhoaHoc;
// import com.duantn.services.GiaoDichService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import java.util.List;

// @Controller
// @RequestMapping("/hoc-vien")
// public class LichSuGiaoDichController {

//     @Autowired
//     private GiaoDichService giaoDichService;

//     @GetMapping("/lich-su-giao-dich")
//     public String lichSuGiaoDich(Model model) {
//         try {
//             // Lấy dữ liệu từ bảng giao dịch chính
//             List<GiaoDichKhoaHoc> giaoDichList = giaoDichService.getAllGiaoDich();
            
//             model.addAttribute("giaoDichList", giaoDichList);
            
//             // Thêm thông báo debug
//             if (giaoDichList.isEmpty()) {
//                 model.addAttribute("debugMessage", "Không tìm thấy giao dịch nào trong database.");
//             } else {
//                 model.addAttribute("debugMessage", "Tìm thấy " + giaoDichList.size() + " giao dịch.");
//             }
            
//             return "views/gdienHocVien/lich-su-giao-dich";
//         } catch (Exception e) {
//             e.printStackTrace();
//             model.addAttribute("giaoDichList", List.of());
//             model.addAttribute("error", "Lỗi khi tải lịch sử giao dịch: " + e.getMessage());
//             return "views/gdienHocVien/lich-su-giao-dich";
//         }
//     }
// } 