package com.duantn.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.repositories.BaiGiangRepository;
import com.duantn.services.BaiGiangService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BaiGiangServiceImpl implements BaiGiangService {
    @Autowired
    private BaiGiangRepository baiGiangRepositories;

    @Override
    public BaiGiang findBaiGiangById(Integer id) {
        return baiGiangRepositories.findById(id).orElse(null);
    }

    @Override
    public BaiGiang save(BaiGiang baiGiang) {
        return baiGiangRepositories.save(baiGiang);
    }

    @Override
    public void xoaBaiGiangTheoId(Integer id) {
        BaiGiang baiGiang = baiGiangRepositories.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài giảng với ID: " + id));
        baiGiangRepositories.delete(baiGiang);
    }

    @Override
    public List<BaiGiang> findByChuongId(Integer chuongId) {
        return baiGiangRepositories.findByChuong_ChuongId(chuongId);
    }

    @Override
    public Optional<BaiGiang> findById(Integer id) {
        return baiGiangRepositories.findById(id);
    }

    @Override
    public BaiGiang findByTracNghiemId(Integer baiTracNghiemId) {
        return baiGiangRepositories.findByTracNghiemId(baiTracNghiemId);
    }

    @Override
    public BaiGiang getById(Integer baiGiangId) {
        return baiGiangRepositories.findById(baiGiangId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài giảng với ID: " + baiGiangId));
    }

    @Override
    public int countByChuongId(Integer chuongId) {
        return baiGiangRepositories.countByChuong_ChuongId(chuongId);
    }

    @Override
    public int demSoBaiGiang(Integer khoahocId) {
        return baiGiangRepositories.countByKhoahocId(khoahocId);
    }

}
