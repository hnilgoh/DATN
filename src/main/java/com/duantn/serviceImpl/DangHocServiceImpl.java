package com.duantn.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.DangHoc;
import com.duantn.entities.KhoaHoc;
import com.duantn.repositories.DangHocRepository;
import com.duantn.services.DangHocService;

@Service
public class DangHocServiceImpl implements DangHocService {

    @Autowired
    private DangHocRepository dangHocRepository;

    @Override
    public long demSoLuongDangKy(Integer khoaHocId) {
        return dangHocRepository.countByKhoahoc_KhoahocId(khoaHocId);
    }

    @Override
    public DangHoc findByTaiKhoanIdAndKhoaHocId(Integer taiKhoanId, Integer khoaHocId) {
        return dangHocRepository.findByTaikhoan_TaikhoanIdAndKhoahoc_KhoahocId(taiKhoanId, khoaHocId);
    }

    @Override
    public boolean isEnrolled(Integer taiKhoanId, Integer khoaHocId) {
        DangHoc dangHoc = findByTaiKhoanIdAndKhoaHocId(taiKhoanId, khoaHocId);
        return dangHoc != null;
    }

    @Override
    public boolean existsByKhoaHocId(Integer khoaHocId) {
        return dangHocRepository.existsByKhoahoc_KhoahocId(khoaHocId);
    }

    @Override
    public List<KhoaHoc> findKhoaHocByHocVienId(Integer hocVienId) {
        return dangHocRepository.findKhoaHocByHocVienId(hocVienId);
    }
}
