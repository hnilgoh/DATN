package com.duantn.services;

import com.duantn.entities.GiaoDichKhoaHoc;
import com.duantn.entities.GiaoDichKhoaHocChiTiet;
import com.duantn.repositories.GiaoDichKhoaHocChiTietRepository;
import com.duantn.repositories.GiaoDichKhoaHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiaoDichService {

    @Autowired
    private GiaoDichKhoaHocChiTietRepository giaoDichKhoaHocChiTietRepository;

    @Autowired
    private GiaoDichKhoaHocRepository giaoDichKhoaHocRepository;

    public List<GiaoDichKhoaHocChiTiet> getAllGiaoDichChiTiet() {
        return giaoDichKhoaHocChiTietRepository.findAllWithDetails();
    }

    // Sửa lại để chỉ lấy giao dịch trạng thái HOAN_THANH
    public List<GiaoDichKhoaHoc> getAllGiaoDich() {
        return giaoDichKhoaHocRepository.findAllHoanThanhWithDetails();
    }

    public GiaoDichKhoaHoc getGiaoDichById(Integer id) {
        Optional<GiaoDichKhoaHoc> giaoDichOptional = giaoDichKhoaHocRepository.findByIdWithDetails(id);
        return giaoDichOptional.orElse(null);
    }

    public List<GiaoDichKhoaHocChiTiet> getLichSuGiaoDichByAccountId(Integer accountId) {
        return giaoDichKhoaHocChiTietRepository.findByAccountId(accountId);
    }

    public List<GiaoDichKhoaHoc> findByTaiKhoanId(Integer taiKhoanId) {
        return giaoDichKhoaHocRepository.findByTaikhoan_TaikhoanId(taiKhoanId);
    }
}
