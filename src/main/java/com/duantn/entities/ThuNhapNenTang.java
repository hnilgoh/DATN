package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ThuNhapNenTang")
public class ThuNhapNenTang implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thuNhapNenTangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danghocId", nullable = false)
    private DangHoc dangHoc;

    @Column(name = "sotiennhan", precision = 12, scale = 2, nullable = false)
    private BigDecimal sotiennhan;

    @CreationTimestamp
    @Column(name = "ngaynhan", updatable = false)
    private LocalDateTime ngaynhan;

    @Column(name = "tenKhoaHoc", columnDefinition = "NVARCHAR(MAX)")
    private String tenKhoaHoc;

    @Column(name = "thuocGiangVien", columnDefinition = "NVARCHAR(MAX)")
    private String thuocGiangVien;
}
