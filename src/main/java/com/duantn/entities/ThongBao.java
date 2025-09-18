package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.duantn.enums.LoaiThongBao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ThongBao")
public class ThongBao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thongBaoId;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String tieuDe;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngaygui;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiThongBao")
    private LoaiThongBao loaiThongBao;

    @ManyToMany
    @JoinTable(name = "thong_bao_nguoi_nhan", joinColumns = @JoinColumn(name = "thong_bao_id"), inverseJoinColumns = @JoinColumn(name = "tai_khoan_id"))
    private List<TaiKhoan> nguoiNhan;

}
