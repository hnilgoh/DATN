package com.duantn.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.VideoBaiGiang;
import com.duantn.repositories.VideoBaiGiangRepository;
import com.duantn.services.VideoBaiGiangService;

@Service
public class VideoBaiGiangServiceImpl implements VideoBaiGiangService {

    @Autowired
    VideoBaiGiangRepository videobaiGiangRepository;

    @Override
    public void save(VideoBaiGiang video) {
        BaiGiang baiGiang = video.getBaiGiang();

        if (baiGiang.getBaiViet() != null || baiGiang.getTracNghiem() != null) {

            throw new IllegalStateException(
                    "Bài giảng đã có nội dung khác (bài viết hoặc trắc nghiệm). Vui lòng xóa trước khi thêm video.");
        }
        videobaiGiangRepository.save(video);
    }

    @Override
    public VideoBaiGiang findByBaiGiangId(Integer baiGiangId) {
        return videobaiGiangRepository.findByBaiGiang_BaiGiangId(baiGiangId);
    }

    @Override
    public VideoBaiGiang findByBaiGiang(BaiGiang baiGiang) {
        return videobaiGiangRepository.findByBaiGiang(baiGiang);
    }

}
