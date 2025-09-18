package com.duantn.services;

import java.util.List;

import com.duantn.entities.DangHoc;
import com.duantn.entities.TienDoHoc;

public interface TienDoHocService {
    List<TienDoHoc> findByDangHocId(Integer dangHocId);

    void taoTienDoChoDangHoc(DangHoc dangHoc);

    void capNhatTienDoSauKhiHoc(Integer taiKhoanId, Integer baiGiangId);

    int tinhTienDoPhanTram(Integer taiKhoanId, Integer khoaHocId);
}
