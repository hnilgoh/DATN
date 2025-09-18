package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.DangHoc;
import com.duantn.entities.TienDoHoc;

@Repository
public interface TienDoHocRepository extends JpaRepository<TienDoHoc, Integer> {
    List<TienDoHoc> findByDangHoc_DanghocId(Integer danghocId);

    boolean existsByDangHocAndBaiGiang(DangHoc dangHoc, BaiGiang baiGiang);

    TienDoHoc findByDangHoc_DanghocIdAndBaiGiang_BaiGiangId(Integer dangHocId, Integer baiGiangId);

    @Query("""
                SELECT COUNT(t) FROM TienDoHoc t
                WHERE t.dangHoc.khoahoc.khoahocId = :khoaHocId
                AND t.dangHoc.taikhoan.taikhoanId = :taikhoanId
                AND t.trangthai = true
            """)
    int demSoBaiHoanThanhTheoKhoaHocVaTaiKhoan(
            @Param("khoaHocId") Integer khoaHocId,
            @Param("taikhoanId") Integer taikhoanId);

}