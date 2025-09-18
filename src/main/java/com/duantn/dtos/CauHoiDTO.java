package com.duantn.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CauHoiDTO {
    private Integer cauHoiId;
    private String tenCauHoi;
    private String giaiThich;
    private Integer dapAnDungIndex; // 0 = A, 1 = B, ...
    private List<DapAnDto> dapAnList = new ArrayList<>();
    private Integer baiGiangId;
}