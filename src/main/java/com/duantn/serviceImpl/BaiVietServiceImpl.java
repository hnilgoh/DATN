package com.duantn.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BaiViet;
import com.duantn.repositories.BaiVietRepository;
import com.duantn.services.BaiVietService;

@Service
public class BaiVietServiceImpl implements BaiVietService {
    @Autowired
    BaiVietRepository baiVietRepository;

    @Override
    public BaiViet save(BaiViet baiviet) {

        BaiGiang baiGiang = baiviet.getBaiGiang();

        if (baiGiang.getVideoBaiGiang() != null || baiGiang.getTracNghiem() != null) {
            throw new IllegalStateException(
                    "Bài giảng đã có nội dung khác (video hoặc trắc nghiệm). Vui lòng xóa trước khi thêm bài viết.");
        }
        return baiVietRepository.save(baiviet);
    }

    @Override
    public BaiViet findById(Integer id) {
        return baiVietRepository.findById(id).orElse(null);
    }

    @Override
    public BaiViet findByBaiGiangId(Integer baiGiangId) {
        return baiVietRepository.findByBaiGiang_BaiGiangId(baiGiangId).orElse(null);
    }

}
