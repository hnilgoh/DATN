package com.duantn.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.TaiKhoan;
import com.duantn.entities.ThongBao;
import com.duantn.enums.LoaiThongBao;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.ThongBaoRepository;
import com.duantn.services.ThongBaoService;

@Service
public class ThongBaoServiceImpl implements ThongBaoService {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public void guiThongBao(String tieuDe, String noiDung, List<Integer> danhSachTaiKhoanId) {
        List<TaiKhoan> nguoiNhan = taiKhoanRepository.findAllById(danhSachTaiKhoanId);

        ThongBao thongBao = ThongBao.builder()
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .ngaygui(LocalDateTime.now())
                .nguoiNhan(nguoiNhan)
                .loaiThongBao(LoaiThongBao.KHOA_HOC)
                .build();

        thongBaoRepository.save(thongBao);
    }
}
