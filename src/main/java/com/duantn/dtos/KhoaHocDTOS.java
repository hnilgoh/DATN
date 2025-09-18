package com.duantn.dtos;

import com.duantn.entities.KhoaHoc;

import lombok.Data;

@Data
public class KhoaHocDTOS {
    private KhoaHoc khoaHoc;
    private int soHocVien;
    private double doanhThu;

    public KhoaHocDTOS(KhoaHoc khoaHoc, int soHocVien, double doanhThu) {
        this.khoaHoc = khoaHoc;
        this.soHocVien = soHocVien;
        this.doanhThu = doanhThu;
    }

    // getter, setter
}
