package com.duantn.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DangKyGiangVienDto {
    @NotBlank(message = "Vui lòng nhập kỹ năng")
    private String kyNang;

    @NotBlank(message = "Vui lòng nhập kinh nghiệm")
    private String kinhNghiem;

    @NotBlank(message = "Vui lòng nhập CCCD")
    @Pattern(regexp = "^[0-9]{12}$", message = "CCCD phải là 12 chữ số")
    private String CCCD;

    @NotBlank(message = "Vui lòng nhập công việc hiện tại")
    private String congViec;

    @NotNull(message = "Vui lòng chọn ngày sinh")
    @Past(message = "Ngày sinh không hợp lệ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    @NotBlank(message = "Vui lòng chọn giới tính")
    private String gioiTinh;

    @NotBlank(message = "Vui lòng nhập chuyên ngành")
    private String chuyenNganh;
}
