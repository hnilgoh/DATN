package com.duantn.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.dtos.KetQuaChiTietDto;
import com.duantn.dtos.KetQuaRequestDto;
import com.duantn.entities.*;
import com.duantn.repositories.*;
import com.duantn.services.KetQuaService;

import jakarta.transaction.Transactional;

@Service
public class KetQuaServiceImpl implements KetQuaService {

        @Autowired
        private KetQuaRepository ketQuaRepo;
        @Autowired
        private KetQuaChiTietRepository chiTietRepo;
        @Autowired
        private TaiKhoanRepository taiKhoanRepo;
        @Autowired
        private BaiTracNghiemRepository baiTracRepo;
        @Autowired
        private CauHoiRepository cauHoiRepo;
        @Autowired
        private DapAnRepository dapAnRepo;

        @Override
        @Transactional
        public KetQua luuKetQua(KetQuaRequestDto dto) {
                TaiKhoan tk = taiKhoanRepo.findById(dto.getTaiKhoanId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
                BaiTracNghiem btn = baiTracRepo.findById(dto.getBaiTracNghiemId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài trắc nghiệm"));

                Optional<KetQua> ketQuaOptional = ketQuaRepo
                                .findByTaikhoan_TaikhoanIdAndBaitracnghiem_TracnghiemId(dto.getTaiKhoanId(),
                                                dto.getBaiTracNghiemId());

                KetQua ketQua;

                if (ketQuaOptional.isPresent()) {
                        ketQua = ketQuaOptional.get();
                        ketQua.setThoiGianBatDau(dto.getThoiGianBatDau());
                        ketQua.setThoiGianKetThuc(dto.getThoiGianKetThuc());
                        ketQua.setSoCauDung(dto.getSoCauDung());
                        ketQua.setTongDiem(dto.getTongDiem());

                        chiTietRepo.deleteByKetQua_KetquaId(ketQua.getKetquaId());

                } else {
                        ketQua = KetQua.builder()
                                        .taikhoan(tk)
                                        .baitracnghiem(btn)
                                        .thoiGianBatDau(dto.getThoiGianBatDau())
                                        .thoiGianKetThuc(dto.getThoiGianKetThuc())
                                        .soCauDung(dto.getSoCauDung())
                                        .tongDiem(dto.getTongDiem())
                                        .build();
                }

                ketQua = ketQuaRepo.save(ketQua);

                List<KetQuaChiTiet> list = new ArrayList<>();
                for (KetQuaChiTietDto ctDto : dto.getChiTietList()) {
                        CauHoi ch = cauHoiRepo.findById(ctDto.getCauHoiId()).orElse(null);
                        DapAn da = dapAnRepo.findById(ctDto.getDapAnId()).orElse(null);

                        KetQuaChiTiet ct = KetQuaChiTiet.builder()
                                        .ketQua(ketQua)
                                        .cauHoi(ch)
                                        .dapAn(da)
                                        .dapAnChon(ctDto.getDapAnChon())
                                        .dungHaySai(ctDto.getDungHaySai())
                                        .diem(ctDto.getDiem())
                                        .build();
                        list.add(ct);
                }
                chiTietRepo.saveAll(list);
                return ketQua;
        }

        @Override
        public List<Map<String, Object>> layChiTietTheoKetQuaId(Integer ketQuaId) {
                List<KetQuaChiTiet> danhSach = chiTietRepo.findByKetQua_KetquaId(ketQuaId);

                return danhSach.stream().map(ct -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("noiDungCauHoi", ct.getCauHoi().getTenCauHoi());
                        map.put("noiDungDapAnDung", ct.getCauHoi().getDapAnList().stream()
                                        .filter(DapAn::getDapAnDung)
                                        .findFirst()
                                        .map(DapAn::getNoiDungDapAn)
                                        .orElse("Không rõ"));

                        map.put("noiDungDapAnChon", ct.getDapAn().getNoiDungDapAn());
                        map.put("dungHaySai", ct.getDungHaySai());
                        map.put("diem", ct.getDiem());

                        map.put("giaiThich", ct.getCauHoi().getDapAnList().stream()
                                        .filter(DapAn::getDapAnDung)
                                        .findFirst()
                                        .map(DapAn::getGiaThichDapan)
                                        .orElse("Không có giải thích"));
                        return map;
                }).toList();
        }

        @Override
        public Optional<KetQua> findByTaiKhoanIdAndTracNghiemId(Integer taiKhoanId, Integer tracnghiemId) {
                return ketQuaRepo.findByTaikhoan_TaikhoanIdAndBaitracnghiem_TracnghiemId(taiKhoanId, tracnghiemId);
        }

        @Override
        public void xoaChiTietTheoKetQuaId(Integer ketQuaId) {
                chiTietRepo.deleteByKetQua_KetquaId(ketQuaId);
        }

}