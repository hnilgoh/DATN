package com.duantn.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.ChungChi;

@Repository
public interface ChungChiRepository extends JpaRepository<ChungChi, Integer> {
    boolean existsByDanghoc_DanghocId(Integer dangHocId);

    Optional<ChungChi> findByDanghoc_DanghocId(Integer danghocId);
}
