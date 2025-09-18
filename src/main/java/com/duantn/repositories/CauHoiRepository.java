package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.CauHoi;

@Repository
public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {
    List<CauHoi> findAllByBaiTracNghiem_TracnghiemId(Integer tracnghiemId);

}
