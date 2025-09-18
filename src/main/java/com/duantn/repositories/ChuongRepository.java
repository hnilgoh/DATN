package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.Chuong;

@Repository
public interface ChuongRepository extends JpaRepository<Chuong, Integer> {

        // interface ChuongRepository.java
        List<Chuong> findByKhoahoc_KhoahocIdOrderByThutuchuongAsc(Integer khoahocId);

        List<Chuong> findByKhoahoc_KhoahocId(Integer khoahocId);

        List<Chuong> findByKhoahoc_KhoahocIdAndThutuchuongGreaterThanOrderByThutuchuongAsc(Integer khoahocId,
                        int thutuchuong);

        @Query("""
                        SELECT DISTINCT c FROM Chuong c
                        LEFT JOIN FETCH c.baiGiangs bg
                        LEFT JOIN FETCH bg.videoBaiGiang
                        LEFT JOIN FETCH bg.baiViet
                        LEFT JOIN FETCH bg.tracNghiem tn
                        WHERE c.khoahoc.khoahocId = :khoahocId
                        """)
        List<Chuong> findFullByKhoaHocId(@Param("khoahocId") Integer khoahocId);

        //
        int countByKhoahoc_KhoahocId(Integer khoahocId);

}
