package com.duantn.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BaiTracNghiem;
import com.duantn.entities.CauHoi;
import com.duantn.entities.DapAn;
import com.duantn.repositories.BaiTracNghiemRepository;
import com.duantn.services.BaiTracNghiemService;
import com.duantn.services.CauHoiService;
import com.duantn.services.DapAnService;

@Service
public class BaiTracNghiemServiceImpl implements BaiTracNghiemService {

    @Autowired
    BaiTracNghiemRepository baiTracNghiemRepository;

    @Autowired
    private CauHoiService cauHoiService;

    @Autowired
    private DapAnService dapAnService;

    @Override
    public BaiTracNghiem save(BaiTracNghiem tracNghiem) {
        return baiTracNghiemRepository.save(tracNghiem);
    }

    @Override
    public BaiTracNghiem findByBaiGiangId(Integer baiGiangId) {
        return baiTracNghiemRepository.findByBaiGiang_BaiGiangId(baiGiangId);
    }

    @Override
    public BaiTracNghiem findById(Integer id) {
        return baiTracNghiemRepository.findById(id).orElse(null);
    }

    // @Override
    // public BaiTracNghiem findFullByBaiGiangId(Integer baiGiangId) {
    // return
    // baiTracNghiemRepository.findByBaiGiang_BaiGiangIdFetchCauHoiVaDapAn(baiGiangId);
    // }

    // @Override
    // @Transactional(readOnly = true)
    // public BaiTracNghiem findFullById(Integer id) {
    // BaiTracNghiem trac = baiTracNghiemRepository.findWithCauHoi(id).orElse(null);
    // if (trac == null || trac.getCauHoiList().isEmpty())
    // return trac;

    // // Lấy list ID câu hỏi
    // List<Integer> cauHoiIds = trac.getCauHoiList().stream()
    // .map(CauHoi::getCauHoiId)
    // .toList();

    // // Lấy tất cả đáp án thuộc những câu hỏi này
    // List<DapAn> allDapAn = dapAnService.findByCauHoiIds(cauHoiIds);

    // // Group đáp án theo câu hỏiId
    // Map<Integer, List<DapAn>> map = allDapAn.stream()
    // .collect(Collectors.groupingBy(da -> da.getCauHoi().getCauHoiId(),
    // LinkedHashMap::new, Collectors.toList()));

    // // Gắn lại đáp án vào từng câu hỏi (không phát sinh N+1)
    // for (CauHoi ch : trac.getCauHoiList()) {
    // List<DapAn> list = map.getOrDefault(ch.getCauHoiId(), List.of());
    // ch.setDapAnList(list);
    // }

    // return trac;
    // }

    @Override
    public BaiTracNghiem findFullByBaiGiangId(Integer baiGiangId) {
        BaiTracNghiem trac = baiTracNghiemRepository.findWithCauHoi(baiGiangId);
        if (trac == null)
            return null;

        List<Integer> cauHoiIds = trac.getCauHoiList()
                .stream()
                .map(CauHoi::getCauHoiId)
                .toList();

        List<DapAn> allDapAn = cauHoiIds.isEmpty()
                ? Collections.emptyList()
                : dapAnService.findByCauHoiIds(cauHoiIds);

        Map<Integer, List<DapAn>> grouped = allDapAn.stream()
                .collect(Collectors.groupingBy(d -> d.getCauHoi().getCauHoiId()));

        for (CauHoi ch : trac.getCauHoiList()) {
            List<DapAn> dapAnList = grouped.getOrDefault(ch.getCauHoiId(), new ArrayList<>());
            ch.setDapAnList(dapAnList);

            // ✅ Gán dapAnDungIndex và giaiThich từ đáp án đúng
            for (int i = 0; i < dapAnList.size(); i++) {
                DapAn da = dapAnList.get(i);
                if (Boolean.TRUE.equals(da.getDapAnDung())) {
                    ch.setDapAnDungIndex(i);
                    ch.setGiaiThich(da.getGiaThichDapan());
                    break;
                }
            }
        }

        return trac;
    }

    @Override
    public void saveBaiTracNghiemVaCauHoi(BaiTracNghiem tracMoi, BaiGiang baiGiang) {
        if (baiGiang.getVideoBaiGiang() != null || baiGiang.getBaiViet() != null) {
            throw new IllegalStateException(
                    "Bài giảng đã có nội dung khác (video hoặc bài viết). Vui lòng xóa trước khi thêm trắc nghiệm.");
        }

        tracMoi.setBaiGiang(baiGiang);
        BaiTracNghiem tracCu = findByBaiGiangId(baiGiang.getBaiGiangId());
        BaiTracNghiem tracLuu = (tracCu != null) ? save(updateTracNghiem(tracCu, tracMoi)) : save(tracMoi);

        if (tracMoi.getCauHoiList() == null)
            return;

        List<CauHoi> cauHoiCuList = cauHoiService.findByBaiTracNghiemId(tracLuu.getTracnghiemId());
        Map<Integer, CauHoi> cauHoiCuMap = cauHoiCuList.stream()
                .filter(ch -> ch.getCauHoiId() != null)
                .collect(Collectors.toMap(CauHoi::getCauHoiId, ch -> ch));

        Set<Integer> cauHoiMoiIds = new HashSet<>();
        int stt = 0;

        for (CauHoi chMoi : tracMoi.getCauHoiList()) {
            chMoi.setBaiTracNghiem(tracLuu);
            chMoi.setCauHoiSo(++stt);

            CauHoi chLuu = (chMoi.getCauHoiId() != null && cauHoiCuMap.containsKey(chMoi.getCauHoiId()))
                    ? updateCauHoi(cauHoiCuMap.get(chMoi.getCauHoiId()), chMoi)
                    : chMoi;

            chLuu = cauHoiService.saveAndFlush(chLuu);
            cauHoiMoiIds.add(chLuu.getCauHoiId());

            // ✅ Tách riêng xử lý đáp án
            xuLyDapAnCauHoi(chMoi, chLuu);
        }

        for (CauHoi chCu : cauHoiCuList) {
            if (!cauHoiMoiIds.contains(chCu.getCauHoiId())) {
                cauHoiService.deleteById(chCu.getCauHoiId());
            }
        }
    }

    private void xuLyDapAnCauHoi(CauHoi chMoi, CauHoi chLuu) {
        List<DapAn> dapAnFormList = chMoi.getDapAnList();
        Integer dapAnDungIndex = chMoi.getDapAnDungIndex();

        if (dapAnDungIndex == null || dapAnFormList == null || dapAnFormList.size() != 4) {
            System.out.println("⚠️ Bỏ qua câu hỏi không đủ dữ liệu: " + chMoi.getTenCauHoi());
            return;
        }

        List<DapAn> dapAnCuList = dapAnService.findByCauHoiId(chLuu.getCauHoiId());
        Map<Integer, DapAn> dapAnCuMap = dapAnCuList.stream()
                .filter(d -> d.getDapanId() != null)
                .collect(Collectors.toMap(DapAn::getDapanId, d -> d));

        Set<Integer> dapAnMoiIds = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            DapAn dapAnForm = dapAnFormList.get(i);

            DapAn da = (dapAnForm.getDapanId() != null && dapAnCuMap.containsKey(dapAnForm.getDapanId()))
                    ? dapAnCuMap.get(dapAnForm.getDapanId())
                    : new DapAn();

            da.setCauHoi(chLuu);
            da.setThuTuDapAn(i + 1);
            da.setNoiDungDapAn(dapAnForm.getNoiDungDapAn());
            da.setDapAnDung(i == dapAnDungIndex);
            da.setGiaThichDapan(i == dapAnDungIndex ? chMoi.getGiaiThich() : null);
            da.setTrangthai(true);

            da = dapAnService.save(da);
            dapAnMoiIds.add(da.getDapanId());
        }

        for (DapAn daCu : dapAnCuList) {
            if (!dapAnMoiIds.contains(daCu.getDapanId())) {
                dapAnService.deleteById(daCu.getDapanId());
            }
        }
    }

    // Phụ trợ: Cập nhật bài trắc nghiệm cũ
    private BaiTracNghiem updateTracNghiem(BaiTracNghiem cu, BaiTracNghiem moi) {
        cu.setTenbai(moi.getTenbai());
        cu.setTrangthai(moi.getTrangthai());
        return cu;
    }

    // Phụ trợ: Cập nhật câu hỏi cũ
    private CauHoi updateCauHoi(CauHoi cu, CauHoi moi) {
        cu.setTenCauHoi(moi.getTenCauHoi());
        cu.setCauHoiSo(moi.getCauHoiSo());
        cu.setTrangthai(moi.getTrangthai());
        return cu;
    }

}
