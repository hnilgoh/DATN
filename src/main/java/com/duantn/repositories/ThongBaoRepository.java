package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.ThongBao;
import com.duantn.enums.LoaiThongBao;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    @Query("SELECT tb FROM ThongBao tb JOIN tb.nguoiNhan nguoi WHERE nguoi.taikhoanId = :taiKhoanId")
    List<ThongBao> findAllByNguoiNhanId(@Param("taiKhoanId") Integer taiKhoanId);

    List<ThongBao> findAllByLoaiThongBao(LoaiThongBao loaiThongBao);

    List<ThongBao> findAllByNguoiNhanTaikhoanIdOrderByNgayguiDesc(Integer nguoiNhanId);

}
