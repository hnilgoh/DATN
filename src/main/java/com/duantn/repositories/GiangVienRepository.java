package com.duantn.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.dtos.DoanhThuKhoaHocGiangVienDto;
import com.duantn.entities.GiangVien;
import com.duantn.entities.TaiKhoan;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, Integer> {

        Optional<GiangVien> findByTaikhoan(TaiKhoan taikhoan);

        //
        Optional<GiangVien> findByTaikhoan_TaikhoanId(Integer id);

        GiangVien getByTaikhoan(TaiKhoan taiKhoan);

        //
        // Tìm kiếm giảng viên theo tên (không phân biệt hoa thường)
        @Query("SELECT gv FROM GiangVien gv WHERE LOWER(gv.taikhoan.name) LIKE LOWER(CONCAT('%', :ten, '%'))")
        java.util.List<GiangVien> findByTenGiangVienContainingIgnoreCase(
                        @org.springframework.data.repository.query.Param("ten") String ten);

        // Tìm kiếm giảng viên theo tên (native query, chắc chắn hoạt động)
        @Query(value = "SELECT * FROM GiangVien gv JOIN TaiKhoan tk ON gv.taikhoanId = tk.taikhoanId WHERE LOWER(tk.name) LIKE LOWER(CONCAT('%', :ten, '%'))", nativeQuery = true)
        java.util.List<GiangVien> findByTenGiangVienContainingIgnoreCaseNative(
                        @org.springframework.data.repository.query.Param("ten") String ten);

        // Thống kê doanh thu từng khóa học của giảng viên
        @Query("SELECT new com.duantn.dtos.DoanhThuKhoaHocGiangVienDto(" +
                        "kh.tenKhoaHoc, COUNT(dh.danghocId), SUM(gtkh.tongtien), SUM(dtgv.sotiennhan)) " +
                        "FROM GiangVien gv " +
                        "JOIN KhoaHoc kh ON gv.giangvienId = kh.giangVien.giangvienId " +
                        "JOIN DangHoc dh ON kh.khoahocId = dh.khoahoc.khoahocId " +
                        "JOIN GiaoDichKhoaHoc gtkh ON gtkh.taikhoan.taikhoanId = dh.taikhoan.taikhoanId " +
                        "JOIN DoanhThuGiangVien dtgv ON dtgv.dangHoc.danghocId = dh.danghocId " +
                        "WHERE gv.giangvienId = :giangVienId " +
                        "GROUP BY kh.tenKhoaHoc")
        java.util.List<DoanhThuKhoaHocGiangVienDto> thongKeDoanhThuTheoGiangVien(
                        @org.springframework.data.repository.query.Param("giangVienId") Integer giangVienId);

        // Tính điểm đánh giá trung bình của giảng viên
        @Query("SELECT AVG(dg.diemDanhGia) FROM DanhGia dg WHERE dg.khoahoc.giangVien.giangvienId = :giangVienId")
        Double tinhDiemDanhGiaTrungBinh(@Param("giangVienId") Integer giangVienId);

        // Tính doanh thu tháng này (lọc theo ngày trong bảng GiaoDichKhoaHoc)
        @Query("SELECT SUM(gtkh.tongtien) " +
                        "FROM GiaoDichKhoaHoc gtkh " +
                        "JOIN DangHoc dh ON dh.taikhoan.taikhoanId = gtkh.taikhoan.taikhoanId " +
                        "JOIN KhoaHoc kh ON kh.khoahocId = dh.khoahoc.khoahocId " +
                        "WHERE kh.giangVien.giangvienId = :giangVienId " +
                        "AND gtkh.ngayGiaoDich BETWEEN :startDate AND :endDate")
        Double tinhDoanhThuTrongKhoangThoiGian(
                        @Param("giangVienId") Integer giangVienId,
                        @Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate);

        @Query("SELECT COUNT(gv) FROM GiangVien gv")
        int countGiangVien();

        GiangVien findByTaikhoan_Email(String email);

}
