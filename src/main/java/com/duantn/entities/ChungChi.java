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
@Table(name = "ChungChi")
public class ChungChi implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chungchiId;

    @Column(name = "MaChungChi", unique = true, columnDefinition = "NVARCHAR(MAX)")
    private String maChungChi;

    @Column(name = "NgayCap", nullable = false)
    private LocalDateTime ngayCap;

    @Column(name = "DuongDanFile", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String duongDanFile;

    @Column(name = "tenKhoaHoc", columnDefinition = "NVARCHAR(MAX)")
    private String tenKhoaHoc;

    @Column(name = "maHocVien")
    private Integer maHocVien;

    @Column(name = "tenHocVien", columnDefinition = "NVARCHAR(MAX)")
    private String tenHocVien;

    @ManyToOne
    @JoinColumn(name = "danghocId", nullable = false)
    private DangHoc danghoc;
}
