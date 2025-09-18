package com.duantn.repositories;

import com.duantn.entities.KetQua;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KetQuaRepository extends JpaRepository<KetQua, Integer> {

    Optional<KetQua> findByTaikhoan_TaikhoanIdAndBaitracnghiem_TracnghiemId(Integer taikhoanId, Integer tracnghiemId);

}