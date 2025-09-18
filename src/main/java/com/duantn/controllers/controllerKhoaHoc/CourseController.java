// package com.duantn.controllers.controllerKhoaHoc;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import com.duantn.entities.KhoaHoc;
// import com.duantn.repositories.KhoaHocRepository;
// import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor;

// @Controller
// @RequestMapping("/khoa-hoc")
// @RequiredArgsConstructor
// public class CourseController {

// private final KhoaHocRepository courseRepository;

// @GetMapping("/{id}")
// public String getChiTietKhoaHoc(@PathVariable("id") Integer id, Model model) {
// KhoaHoc course = courseRepository.findByIdWithChaptersAndLectures(id).orElseThrow(
// () -> new EntityNotFoundException("Không tìm thấy khóa học với id = " + id));
// model.addAttribute("khoaHoc", course);
// model.addAttribute("chuongs", course.getChuongs());
// return "views/KhoaHoc/xemChiTietKhoaHoc"; // viết đúng tên file html
// }
// }
