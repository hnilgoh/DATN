package com.duantn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.BaiTracNghiem;

@Repository
public interface BaiTracNghiemRepository extends JpaRepository<BaiTracNghiem, Integer> {

    BaiTracNghiem findByBaiGiang_BaiGiangId(Integer baiGiangId);

    @Query("SELECT b FROM BaiTracNghiem b " +
            "JOIN FETCH b.cauHoiList c " +
            "LEFT JOIN FETCH c.dapAnList " +
            "WHERE b.baiGiang.baiGiangId = :baiGiangId")
    BaiTracNghiem findByBaiGiang_BaiGiangIdFetchCauHoiVaDapAn(@Param("baiGiangId") Integer baiGiangId);

    // @Query("""
    // SELECT DISTINCT b FROM BaiTracNghiem b
    // JOIN FETCH b.cauHoiList ch
    // WHERE b.baiGiang.baiGiangId = :id
    // """)
    // BaiTracNghiem findWithCauHoi(@Param("id") Integer baiGiangId);

    @Query("""
                SELECT DISTINCT b FROM BaiTracNghiem b
                LEFT JOIN FETCH b.cauHoiList ch
                WHERE b.baiGiang.baiGiangId = :id
            """)
    BaiTracNghiem findWithCauHoi(@Param("id") Integer baiGiangId);

}
