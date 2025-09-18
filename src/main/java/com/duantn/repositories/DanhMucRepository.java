package com.duantn.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duantn.entities.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    boolean existsByTenDanhMuc(String tenDanhMuc);

    boolean existsByTenDanhMucIgnoreCase(String tenDanhMuc);

    DanhMuc findByTenDanhMucIgnoreCase(String tenDanhMuc);

    Optional<DanhMuc> findBySlug(String slug);

    @Query("SELECT dm " +
            "FROM DanhMuc dm JOIN dm.khoaHocs kh " +
            "GROUP BY dm " +
            "ORDER BY COUNT(kh) DESC")
    List<DanhMuc> findTopDanhMucCoNhieuKhoaHoc(Pageable pageable);

}