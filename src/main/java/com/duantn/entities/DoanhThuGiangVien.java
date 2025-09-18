package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.duantn.enums.TrangThaiDoanhThu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DoanhThuGiangVien")
public class DoanhThuGiangVien implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doanhthuId")
    private Integer doanhthuId;

    @Column(name = "sotiennhan", precision = 12, scale = 2, nullable = false)
    private BigDecimal sotiennhan;

    @CreationTimestamp
    @Column(name = "ngaynhan", updatable = false)
    private LocalDateTime ngaynhan;

    @Column(name = "tenGiangVien", columnDefinition = "NVARCHAR(MAX)")
    private String tenGiangVien;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai", nullable = false)
    private TrangThaiDoanhThu trangthai = TrangThaiDoanhThu.DA_NHAN;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoanGV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danghocId", nullable = false)
    private DangHoc dangHoc;

}
