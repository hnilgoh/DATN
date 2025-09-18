package com.duantn.entities;

import java.io.Serializable;
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
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taikhoanId")
    private Integer taikhoanId;

    @Column(name = "name", columnDefinition = "NVARCHAR(MAX)")
    private String name;

    @Column(name = "email", columnDefinition = "NVARCHAR(MAX)")
    private String email;

    @Column(name = "phone", columnDefinition = "NVARCHAR(MAX)")
    private String phone;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "avatar", columnDefinition = "NVARCHAR(MAX)")
    private String avatar;

    @Column(name = "password", columnDefinition = "NVARCHAR(MAX)")
    private String password;

    @Column(name = "status", nullable = false, columnDefinition = "BIT DEFAULT 1")
    @Builder.Default
    private boolean status = true;

    // ===== Quan hệ =====

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "taikhoan", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GiangVien giangVien;

    @OneToMany(mappedBy = "taikhoanGV", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<DoanhThuGiangVien> danhSachDoanhThu;

    @OneToMany(mappedBy = "taikhoanGV", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<RutTienGiangVien> rutTienGV;

    @OneToMany(mappedBy = "taikhoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<GiaoDichKhoaHoc> giaodich;

    @OneToMany(mappedBy = "taikhoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<KetQua> ketqua;

    @ManyToMany(mappedBy = "nguoiNhan")
    private List<ThongBao> thongBaos;

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "taikhoanId=" + taikhoanId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                ", avatar='" + avatar + '\'' +
                ", status=" + status +
                ", role=" + (role != null ? role.getName() : "null") +
                '}';
    }
}
