package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DapAn")
public class DapAn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dapanId;

    @Column(name = "ThuTu_DapAn")
    private Integer thuTuDapAn;

    @Column(name = "NoiDung_DapAn", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDungDapAn;

    @Column(name = "DapAnDung")
    private Boolean dapAnDung;

    @Column(name = "GiaThich_Dapan", columnDefinition = "NVARCHAR(MAX)")
    private String giaThichDapan;

    @Column(name = "trangthai")
    private Boolean trangthai;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CauHoiId", nullable = false)
    private CauHoi cauHoi;
}
