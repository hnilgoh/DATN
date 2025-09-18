package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CauHoi")
public class CauHoi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cauHoiId;

    @Column(name = "cauHoiSo")
    private Integer cauHoiSo;

    @Column(name = "tenCauHoi", columnDefinition = "NVARCHAR(MAX)")
    private String tenCauHoi;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trangthai")
    private Boolean trangthai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracnghiemId", nullable = false)
    private BaiTracNghiem baiTracNghiem;

    @OneToMany(mappedBy = "cauHoi", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @BatchSize(size = 10)
    @Builder.Default
    private List<DapAn> dapAnList = new ArrayList<>();

    // ---- các trường tạm để xử lý form ----
    @Transient
    private Integer dapAnDungIndex; // 0 → A, 1 → B, 2 → C, 3 → D

    @Transient
    private String giaiThich;
}
