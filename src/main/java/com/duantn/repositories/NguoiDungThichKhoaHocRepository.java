package com.duantn.repositories;

import com.duantn.entities.KhoaHoc;
import com.duantn.entities.NguoiDungThichKhoaHoc;
import com.duantn.entities.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface NguoiDungThichKhoaHocRepository extends JpaRepository<NguoiDungThichKhoaHoc, Integer> {

    Set<NguoiDungThichKhoaHoc> findByTaiKhoan_TaikhoanId(Integer taikhoanId);

    Optional<NguoiDungThichKhoaHoc> findByTaiKhoan_TaikhoanIdAndKhoaHoc_KhoahocId(Integer taikhoanId, Integer khoahocId);

    Optional<NguoiDungThichKhoaHoc> findByTaiKhoanAndKhoaHoc(TaiKhoan taiKhoan, KhoaHoc khoaHoc);
}
