package com.duantn.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.duantn.enums.HinhThucThanhToan;
import com.duantn.enums.TrangThaiThanhToan;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ThanhToan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thanhtoanId;

    @Column(name = "tongtien", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongtien;

    @Column(name = "tenhocvien", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String tenhocvien;

    @Column(name = "NgayThanhToan", nullable = false)
    private LocalDateTime ngayThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(name = "hinhThucThanhToan", nullable = false)
    private HinhThucThanhToan hinhThucThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false)
    private TrangThaiThanhToan trangThai;

    @ManyToOne
    @JoinColumn(name = "giaodichId", nullable = false)
    private GiaoDichKhoaHoc giaoDichKhoaHoc;
}

