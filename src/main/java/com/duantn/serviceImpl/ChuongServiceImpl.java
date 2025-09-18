package com.duantn.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.Chuong;
import com.duantn.entities.KhoaHoc;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.repositories.ChuongRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.services.ChuongService;

@Service
public class ChuongServiceImpl implements ChuongService {
    @Autowired
    ChuongRepository chuongRepository;

    @Autowired
    KhoaHocRepository khoaHocRepository;

    @Override
    public Chuong save(Chuong chuong) {
        return chuongRepository.save(chuong);
    }

    @Override
    public void deleteById(Integer chuongId) {
        Chuong chuong = chuongRepository.findById(chuongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        chuongRepository.delete(chuong);
    }

    @Override
    public Chuong findById(Integer id) {
        return chuongRepository.findById(id).orElse(null);
    }

    @Override
    public List<Chuong> findByKhoaHocId(Integer khoahocId) {
        return chuongRepository.findByKhoahoc_KhoahocId(khoahocId);
    }

    @Override
    public List<Chuong> findByKhoahocIdAndThutuchuongGreaterThanOrderByThutuchuongAsc(Integer khoahocId,
            int thutuchuong) {
        return chuongRepository.findByKhoahoc_KhoahocIdAndThutuchuongGreaterThanOrderByThutuchuongAsc(khoahocId,
                thutuchuong);
    }

    @Override
    public List<Chuong> findFullByKhoaHocId(Integer khoahocId) {
        return chuongRepository.findFullByKhoaHocId(khoahocId);
    }

    @Override
    public List<Chuong> findByKhoahocIdOrderByThutuchuongAsc(Integer khoahocId) {
        return chuongRepository.findByKhoahoc_KhoahocIdOrderByThutuchuongAsc(khoahocId);
    }

    @Override
    public void luuChuongTheoKhoaHoc(Integer khoahocId, Chuong chuong) {
        // Gắn khóa học cho chương
        KhoaHoc khoahoc = khoaHocRepository.findById(khoahocId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khóa học"));

        chuong.setKhoahoc(khoahoc);

        // Gắn lại quan hệ từ bài giảng về chương
        if (chuong.getBaiGiangs() != null) {
            for (BaiGiang bg : chuong.getBaiGiangs()) {
                bg.setChuong(chuong);

                // Nếu có enum dưới dạng String thì cần kiểm tra null để tránh lỗi
                if (bg.getLoaiBaiGiang() == null) {
                    bg.setLoaiBaiGiang(LoaiBaiGiang.VIDEO); // mặc định
                }
            }
        }

        chuongRepository.save(chuong);
    }

    @Override
    public Chuong getReferenceById(Integer id) {
        return chuongRepository.getReferenceById(id);
    }

    @Override
    public int demSoChuong(Integer khoahocId) {
        return chuongRepository.countByKhoahoc_KhoahocId(khoahocId);
    }
}
