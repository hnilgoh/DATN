package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Chuong")
public class Chuong implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chuongId")
    private Integer chuongId;

    @Column(name = "tenchuong", columnDefinition = "NVARCHAR(MAX)")
    private String tenchuong;

    @Column(name = "mota", columnDefinition = "NVARCHAR(MAX)")
    private String mota;

    @Column(name = "thutuchuong")
    private Integer thutuchuong;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trangthai")
    @Builder.Default
    private Boolean trangthai = true;

    @ManyToOne
    @JoinColumn(name = "khoahocId", nullable = false)
    private KhoaHoc khoahoc;

    @OneToMany(mappedBy = "chuong", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    private List<BaiGiang> baiGiangs = new ArrayList<>();

    @Override
    public String toString() {
        return "Chuong{" +
                "chuongId=" + chuongId +
                ", tenchuong='" + tenchuong + '\'' +
                ", mota='" + mota + '\'' +
                ", thutuchuong=" + thutuchuong +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                ", trangthai=" + trangthai +
                ", khoahocId=" + (khoahoc != null ? khoahoc.getKhoahocId() : "null") +
                '}';
    }

}
