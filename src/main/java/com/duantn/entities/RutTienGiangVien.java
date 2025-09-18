package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.duantn.enums.TrangThaiRutTien;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "RutTienGiangVien")
public class RutTienGiangVien implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rutTienId")
    private Integer rutTienId;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoanGV;

    @Column(name = "soTienRut", precision = 12, scale = 2, nullable = false)
    private BigDecimal soTienRut;

    @CreationTimestamp
    @Column(name = "ngayrut", updatable = false)
    private LocalDateTime ngayrut;

    @Column(name = "tenGiangVien", columnDefinition = "NVARCHAR(MAX)")
    private String tenGiangVien;

    @Column(name = "so_tai_khoan", columnDefinition = "NVARCHAR(50)", nullable = true)
    private String soTaiKhoan;

    @Column(name = "ten_ngan_hang", columnDefinition = "NVARCHAR(100)", nullable = true)
    private String tenNganHang;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai", nullable = false)
    private TrangThaiRutTien trangthai = TrangThaiRutTien.DANG_CHO_XU_LY;
}
