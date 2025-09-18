package com.duantn.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HocVienDto {
    private Integer id;
    private String hoTen;
    private String email;
    private String soDienThoai;
}