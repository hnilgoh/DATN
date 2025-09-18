package com.duantn.services;

import com.duantn.entities.BaiViet;

public interface BaiVietService {
    BaiViet save(BaiViet baiviet);

    BaiViet findById(Integer id);

    BaiViet findByBaiGiangId(Integer baiGiangId);

}
