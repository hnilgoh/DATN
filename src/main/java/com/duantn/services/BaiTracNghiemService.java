package com.duantn.services;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BaiTracNghiem;

public interface BaiTracNghiemService {

    BaiTracNghiem save(BaiTracNghiem tracNghiem);

    BaiTracNghiem findByBaiGiangId(Integer baiGiangId);

    void saveBaiTracNghiemVaCauHoi(BaiTracNghiem tracMoi, BaiGiang baiGiang);

    // BaiTracNghiem findFullById(Integer id);

    BaiTracNghiem findFullByBaiGiangId(Integer baiGiangId);

    BaiTracNghiem findById(Integer id);

}
