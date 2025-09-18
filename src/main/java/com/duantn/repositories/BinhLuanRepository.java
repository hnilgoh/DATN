package com.duantn.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.BinhLuan;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {
    List<BinhLuan> findByBaiGiang_BaiGiangIdAndParentIsNullOrderByNgayBinhLuanAsc(Integer baiGiangId);

    List<BinhLuan> findByParent_BinhluanIdOrderByNgayBinhLuanAsc(Integer parentId);

    List<BinhLuan> findByBaiGiang_BaiGiangIdOrderByNgayBinhLuanAsc(Integer baiGiangId);

}