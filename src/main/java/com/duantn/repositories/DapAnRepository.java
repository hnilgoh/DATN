package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.DapAn;

import jakarta.transaction.Transactional;

@Repository
public interface DapAnRepository extends JpaRepository<DapAn, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM DapAn d WHERE d.cauHoi.cauHoiId = :cauHoiId")
    void deleteByCauHoiId(@Param("cauHoiId") Integer cauHoiId);

    // Tìm danh sách đáp án theo câu hỏi
    List<DapAn> findByCauHoi_CauHoiId(Integer cauHoiId);

    // Xoá tất cả đáp án theo câu hỏi (nếu vẫn muốn dùng)
    void deleteByCauHoi_CauHoiId(Integer cauHoiId);

    @Query("""
                SELECT d FROM DapAn d
                WHERE d.cauHoi.cauHoiId IN :ids
            """)
    List<DapAn> findByCauHoiIds(@Param("ids") List<Integer> ids);

}
