package com.duantn.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BaiTracNghiemDto {
    private Integer tracnghiemId;
    private String tenbai;
    private Integer baiGiangId;
    private List<CauHoiDTO> cauHoiList = new ArrayList<>();
}
