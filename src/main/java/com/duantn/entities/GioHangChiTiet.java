package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "GioHangChiTiet")
public class GioHangChiTiet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dongia", precision = 12, scale = 2, nullable = false)
    private BigDecimal dongia;

    @CreationTimestamp
    @Column(name = "NgayThem", nullable = false)
    private LocalDateTime ngayThem;

    @ManyToOne
    @JoinColumn(name = "giohangId", nullable = false)
    private GioHang giohang;

    @ManyToOne
    @JoinColumn(name = "khoahocId", nullable = false)
    private KhoaHoc khoahoc;
}
