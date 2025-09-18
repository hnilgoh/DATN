package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DangHoc")
public class DangHoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer danghocId;

    @CreationTimestamp
    @Column(name = "ngayDangKy", updatable = false)
    private LocalDateTime ngayDangKy;

    @Column(name = "dongia", precision = 12, scale = 2, nullable = false)
    private BigDecimal dongia;

    @Builder.Default
    @Column(name = "trangthai", nullable = false)
    private boolean trangthai = false;

    @Builder.Default
    @Column(name = "dacap_chungchi", nullable = false)
    private boolean daCap_ChungChi = false;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoan;

    @ManyToOne
    @JoinColumn(name = "khoahocId", nullable = false)
    private KhoaHoc khoahoc;

    @OneToMany(mappedBy = "dangHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoanhThuGiangVien> danhSachDoanhThu;

}
