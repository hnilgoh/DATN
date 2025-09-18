package com.duantn.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.duantn.entities.BaiGiang;

@Repository
public interface BaiGiangRepository extends JpaRepository<BaiGiang, Integer> {

    // Lấy danh sách bài giảng theo ID chương
    List<BaiGiang> findByChuong_ChuongId(Integer chuongId);

    @Query("SELECT bg FROM BaiGiang bg WHERE bg.tracNghiem.tracnghiemId = :tracnghiemId")
    BaiGiang findByTracNghiemId(@Param("tracnghiemId") Integer tracnghiemId);

    //
    int countByChuong_ChuongId(Integer chuongId);

    @Query("SELECT COUNT(b) FROM BaiGiang b WHERE b.chuong.khoahoc.khoahocId = :khoahocId")
    int countByKhoahocId(@Param("khoahocId") Integer khoahocId);

    @Query("SELECT bg FROM BaiGiang bg LEFT JOIN FETCH bg.videoBaiGiang WHERE bg.baiGiangId = :id")
    BaiGiang findByIdWithVideo(@Param("id") Integer id);

    int countByChuong_Khoahoc_KhoahocId(Integer khoahocId);

}
