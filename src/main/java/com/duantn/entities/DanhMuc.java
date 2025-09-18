package com.duantn.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DanhMuc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer danhmucId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String tenDanhMuc;

    @Column(unique = true)
    private String slug;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = true;

    @OneToMany(mappedBy = "danhMuc")
    private List<KhoaHoc> khoaHocs;

    @Override
    public String toString() {
        return "DanhMuc{" +
                "danhmucId=" + danhmucId +
                ", tenDanhMuc='" + tenDanhMuc + '\'' +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                '}';
    }
}
