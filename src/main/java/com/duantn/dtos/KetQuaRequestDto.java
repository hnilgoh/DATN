package com.duantn.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class KetQuaRequestDto {
    private Integer taiKhoanId;
    private Integer baiTracNghiemId;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private Integer soCauDung;
    private Double tongDiem;
    private List<KetQuaChiTietDto> chiTietList;
}
