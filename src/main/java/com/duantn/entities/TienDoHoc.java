package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "TienDoHoc")
public class TienDoHoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tiendoId;

    @Column(name = "ngayhoanthanh")
    private LocalDateTime ngayhoanthanh;

    @Column(name = "tenHocVien", columnDefinition = "NVARCHAR(MAX)")
    private String tenHocVien;

    @Column(name = "tenKhoaHoc", columnDefinition = "NVARCHAR(MAX)")
    private String tenKhoaHoc;

    @Builder.Default
    @Column(name = "trangthai", nullable = false)
    private boolean trangthai = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danghocId", nullable = false)
    private DangHoc dangHoc;

    @ManyToOne
    @JoinColumn(name = "baiGiangId", nullable = false)
    private BaiGiang baiGiang;
}
