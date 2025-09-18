package com.duantn.services;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.VideoBaiGiang;

public interface VideoBaiGiangService {

    void save(VideoBaiGiang video);

    VideoBaiGiang findByBaiGiangId(Integer baiGiangId);

    VideoBaiGiang findByBaiGiang(BaiGiang baiGiang);

}
