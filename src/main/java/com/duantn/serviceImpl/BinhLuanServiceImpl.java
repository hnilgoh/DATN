package com.duantn.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BinhLuan;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.BaiGiangRepository;
import com.duantn.repositories.BinhLuanRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.BinhLuanService;

import jakarta.transaction.Transactional;

@Service
public class BinhLuanServiceImpl implements BinhLuanService {
    @Autowired
    private BinhLuanRepository binhLuanRepository;

    @Autowired
    private BaiGiangRepository baiGiangRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository; // Giả định bạn có cách để lấy TaiKhoan hiện tại (ví dụ từ
                                                   // SecurityContext)

    // Lấy tất cả bình luận chính (parent IS NULL) của một bài giảng
    public List<BinhLuan> getCommentsByBaiGiangId(Integer baiGiangId) {
        return binhLuanRepository.findByBaiGiang_BaiGiangIdAndParentIsNullOrderByNgayBinhLuanAsc(baiGiangId);
    }

    // Lấy tất cả bình luận con (replies) của một bình luận cha
    public List<BinhLuan> getRepliesByParentCommentId(Integer parentId) {
        return binhLuanRepository.findByParent_BinhluanIdOrderByNgayBinhLuanAsc(parentId);
    }

    // Đăng bình luận mới
    @Transactional
    public BinhLuan saveComment(Integer baiGiangId, String noiDung, Integer taikhoanId) {
        Optional<BaiGiang> baiGiangOpt = baiGiangRepository.findById(baiGiangId);
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taikhoanId); // Thay thế bằng TaiKhoan của người
                                                                                  // dùng hiện tại

        if (baiGiangOpt.isPresent() && taiKhoanOpt.isPresent()) {
            BinhLuan binhLuan = BinhLuan.builder()
                    .noiDung(noiDung)
                    .baiGiang(baiGiangOpt.get())
                    .taikhoan(taiKhoanOpt.get())
                    .ngayBinhLuan(LocalDateTime.now()) // CreationTimestamp sẽ tự động điền, nhưng có thể set ở đây để
                                                       // đảm bảo
                    .build();
            return binhLuanRepository.save(binhLuan);
        }
        return null; // Hoặc throw exception
    }

    // Trả lời bình luận
    @Transactional
    public BinhLuan replyToComment(Integer baiGiangId, Integer parentCommentId, String noiDung, Integer taikhoanId) {
        Optional<BaiGiang> baiGiangOpt = baiGiangRepository.findById(baiGiangId);
        Optional<BinhLuan> parentCommentOpt = binhLuanRepository.findById(parentCommentId);
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taikhoanId); // Thay thế bằng TaiKhoan của người
                                                                                  // dùng hiện tại

        if (baiGiangOpt.isPresent() && parentCommentOpt.isPresent() && taiKhoanOpt.isPresent()) {
            BinhLuan reply = BinhLuan.builder()
                    .noiDung(noiDung)
                    .baiGiang(baiGiangOpt.get())
                    .taikhoan(taiKhoanOpt.get())
                    .parent(parentCommentOpt.get())
                    .ngayBinhLuan(LocalDateTime.now())
                    .build();
            return binhLuanRepository.save(reply);
        }
        return null; // Hoặc throw exception
    }

    // Xóa bình luận
    @Transactional
    public boolean deleteComment(Integer binhLuanId, Integer currentUserId) {
        Optional<BinhLuan> binhLuanOpt = binhLuanRepository.findById(binhLuanId);
        if (binhLuanOpt.isPresent()) {
            BinhLuan binhLuan = binhLuanOpt.get();
            // Chỉ cho phép xóa bình luận của chính người dùng
            if (binhLuan.getTaikhoan().getTaikhoanId().equals(currentUserId)) {
                // Xóa tất cả các bình luận con trước nếu có (tùy thuộc vào cascade type của
                // bạn)
                // Trong trường hợp này, @ManyToOne cho parent sẽ không tự động xóa con.
                // Nếu muốn xóa recursive, bạn cần triển khai logic xóa con trước hoặc dùng
                // cascade remove trên parent nếu BinhLuan có danh sách con.
                // Hiện tại, tôi sẽ chỉ xóa bình luận được chỉ định.
                // Nếu BinhLuan có @OneToMany<BinhLuan> replies, bạn có thể thiết lập cascade.
                // Để đơn giản, nếu một bình luận có reply, nó sẽ không xóa được hoặc phải xóa
                // hết reply trước.
                // Để dễ dàng, chúng ta sẽ xóa thẳng. Nếu có lỗi khóa ngoại, bạn cần xử lý.
                binhLuanRepository.delete(binhLuan);
                return true;
            }
        }
        return false;
    }

    public Optional<BinhLuan> getCommentById(Integer commentId) {
        return binhLuanRepository.findById(commentId);
    }

    public List<BinhLuan> getAllCommentsByBaiGiangId(Integer baiGiangId) {
        return binhLuanRepository.findByBaiGiang_BaiGiangIdOrderByNgayBinhLuanAsc(baiGiangId);
    }

}