package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "BinhLuan")
public class BinhLuan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer binhluanId;

    @Column(name = "NoiDung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "NgayBinhLuan", nullable = false, updatable = false)
    private LocalDateTime ngayBinhLuan;

    // Comment cha
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BinhLuan parent;

    // Comment con
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinhLuan> replies;

    @ManyToOne
    @JoinColumn(name = "taikhoanId", nullable = false)
    private TaiKhoan taikhoan;

    @ManyToOne
    @JoinColumn(name = "baiGiangId", nullable = false)
    private BaiGiang baiGiang;
}