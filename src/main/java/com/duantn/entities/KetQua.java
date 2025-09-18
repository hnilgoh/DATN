package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "KetQua")
public class KetQua implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ketquaId;

    @Column(name = "ThoiGianBatDau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "ThoiGianKetThuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "TongDiem", nullable = false)
    private Double tongDiem;

    @Column(name = "Socaudung", nullable = false)
    private Integer soCauDung;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoan;

    @ManyToOne
    @JoinColumn(name = "tracnghiemId", nullable = false)
    private BaiTracNghiem baitracnghiem;

    @OneToMany(mappedBy = "ketQua", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KetQuaChiTiet> ketQuaChiTiet;
}
