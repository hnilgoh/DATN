package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "taikhoan")
@Table(name = "GiangVien")
public class GiangVien implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer giangvienId;

    @Column(name = "kyNang", columnDefinition = "NVARCHAR(MAX)")
    private String kyNang;

    @Column(name = "kinhNghiem", columnDefinition = "NVARCHAR(MAX)")
    private String kinhNghiem;

    @Column(name = "CCCD", columnDefinition = "NVARCHAR(MAX)")
    private String CCCD;

    @Column(name = "congViec", columnDefinition = "NVARCHAR(255)")
    private String congViec;

    @Column(name = "ngaySinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    @Column(name = "gioiTinh", columnDefinition = "NVARCHAR(50)")
    private String gioiTinh;

    @Column(name = "chuyenNganh", columnDefinition = "NVARCHAR(255)")
    private String chuyenNganh;

    @CreationTimestamp
    @Column(name = "ngaythamgia", updatable = false)
    private LocalDateTime ngayThamGia;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToOne
    @JoinColumn(name = "taikhoanId", nullable = false, unique = true)
    private TaiKhoan taikhoan;

    @Override
    public String toString() {
        return "GiangVien{" +
                "giangvienId=" + giangvienId +
                ", kyNang='" + kyNang + '\'' +
                ", kinhNghiem='" + kinhNghiem + '\'' +
                ", CCCD='" + CCCD + '\'' +
                ", congViec='" + congViec + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", chuyenNganh='" + chuyenNganh + '\'' +
                ", ngayThamGia=" + ngayThamGia +
                ", ngayCapNhat=" + ngayCapNhat +
                ", taikhoanId=" + (taikhoan != null ? taikhoan.getTaikhoanId() : "null") +
                '}';
    }
}