package com.duantn.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HocVienTheoKhoaHocDto {
    private Integer khoaHocId;
    private String tenKhoaHoc;
    private Long soLuongHocVien;
}
