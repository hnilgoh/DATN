package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.duantn.enums.LoaiBaiGiang;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = { "videoBaiGiang", "baiViet", "tracNghiem" })
@Table(name = "BaiGiang")
public class BaiGiang implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer baiGiangId;

    @Column(name = "tenBaiGiang", columnDefinition = "NVARCHAR(MAX)")
    private String tenBaiGiang;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String mota;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trangthai")
    private Boolean trangthai;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_bai_giang", length = 30)
    private LoaiBaiGiang loaiBaiGiang;

    @ManyToOne
    @JoinColumn(name = "chuongId")
    private Chuong chuong;

    @OneToOne(mappedBy = "baiGiang", cascade = CascadeType.ALL)
    private VideoBaiGiang videoBaiGiang;

    @OneToOne(mappedBy = "baiGiang", cascade = CascadeType.ALL)
    private BaiViet baiViet;

    @OneToOne(mappedBy = "baiGiang", cascade = CascadeType.ALL)
    private BaiTracNghiem tracNghiem;
}
