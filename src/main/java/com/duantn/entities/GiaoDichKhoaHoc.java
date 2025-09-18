package com.duantn.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.duantn.enums.HinhThucThanhToan;
import com.duantn.enums.TrangThaiGiaoDich;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "GiaoDichKhoaHoc")
public class GiaoDichKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer giaodichId;

    @Column(name = "tongtien", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongtien;

    @Column(name = "tenhocvien", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String tenhocvien;

    @CreationTimestamp
    @Column(name = "NgayGiaoDich", nullable = false)
    private LocalDateTime ngayGiaoDich;

    @Enumerated(EnumType.STRING)
    @Column(name = "hinhThucThanhToan", nullable = false)
    private HinhThucThanhToan hinhThucThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrangThaiGiaoDich trangthai;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoan;

    @OneToMany(mappedBy = "giaoDichKhoaHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<GiaoDichKhoaHocChiTiet> chiTietGiaoDich;
}
