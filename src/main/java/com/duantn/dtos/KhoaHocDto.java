package com.duantn.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class KhoaHocDto {

    @NotBlank(message = "Tên khóa học không được để trống.")
    @Size(min = 5, message = "Tên khóa học phải từ 5 ký tự.")
    private String tenKhoaHoc;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 30, message = "Mô tả phải từ 30 ký tự.")
    private String moTa;

    @NotNull(message = "Giá khóa học là bắt buộc.")
    @DecimalMin(value = "1000.00", message = "Giá phải lớn hơn 1000.")
    private BigDecimal giagoc;

    @Min(value = 0, message = "Phần trăm giảm ít nhất là 0.")
    @Max(value = 100, message = "Phần trăm giảm tối đa là 100.")
    private Integer phanTramGiam;

    private BigDecimal giaKhuyenMai;

    private String urlGioiThieu;

    private LocalDateTime ngaybatdau;

    private LocalDateTime ngayketthuc;

    private Integer danhmucId;
}
