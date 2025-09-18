package com.duantn.services;

import java.util.List;

import com.duantn.entities.Chuong;

public interface ChuongService {
    Chuong save(Chuong chuong);

    void deleteById(Integer chuongId);

    Chuong findById(Integer id);

    List<Chuong> findByKhoaHocId(Integer khoahocId);

    List<Chuong> findByKhoahocIdAndThutuchuongGreaterThanOrderByThutuchuongAsc(Integer khoahocId, int thutuchuong);

    List<Chuong> findFullByKhoaHocId(Integer khoahocId);

    List<Chuong> findByKhoahocIdOrderByThutuchuongAsc(Integer khoahocId);

    void luuChuongTheoKhoaHoc(Integer khoahocId, Chuong chuong);

    Chuong getReferenceById(Integer id);

    //

    int demSoChuong(Integer khoahocId);
}
