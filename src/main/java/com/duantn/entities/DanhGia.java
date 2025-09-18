package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DanhGia", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "nguoiDungId", "khoaHocId" })
})
public class DanhGia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer danhgiaId;

    @Column(name = "NoiDung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "ngayDanhGia", nullable = false, updatable = false)
    private LocalDateTime ngayDanhGia;

    @Column(name = "DiemDanhGia", nullable = false)
    private Integer diemDanhGia;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoan;

    @ManyToOne
    @JoinColumn(name = "khoahocId", nullable = false)
    private KhoaHoc khoahoc;
}
