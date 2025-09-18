package com.duantn.configs;

import com.duantn.entities.ThongBao;
import com.duantn.entities.VerificationToken;
import com.duantn.enums.LoaiThongBao;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.ThongBaoRepository;
import com.duantn.repositories.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenCleanupTask {

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private ThongBaoRepository thongBaoRepo;

    /**
     * Hàm này chạy mỗi ngày lúc 1h sáng để xóa token và tài khoản chưa xác thực quá
     * hạn 2 ngày.
     */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void xoaTaiKhoanChuaXacThucHetHan() {
        List<VerificationToken> hetHanTokens = tokenRepo.findByExpiryTimeBefore(LocalDateTime.now());

        for (VerificationToken token : hetHanTokens) {
            taiKhoanRepo.findByEmail(token.getEmail()).ifPresent(taiKhoan -> {
                if (!taiKhoan.isStatus()) {
                    thongBaoRepo.save(
                            ThongBao.builder()
                                    .tieuDe("Tài khoản bị xóa do không xác thực")
                                    .noiDung("Tài khoản với email " + taiKhoan.getEmail()
                                            + " đã bị xóa do không xác thực sau 2 ngày.")
                                    .loaiThongBao(LoaiThongBao.HE_THONG)
                                    .build());
                    taiKhoanRepo.delete(taiKhoan);
                }
            });
            tokenRepo.delete(token);
        }
    }
}