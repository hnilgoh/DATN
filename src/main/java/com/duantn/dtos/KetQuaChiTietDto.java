package com.duantn.dtos;

import lombok.Data;

@Data
public class KetQuaChiTietDto {
    private Integer cauHoiId;
    private Integer dapAnId;
    private Integer dapAnChon;
    private Boolean dungHaySai;
    private Double diem;
    private String giaiThich;
}
