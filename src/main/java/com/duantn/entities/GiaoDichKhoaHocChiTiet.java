package com.duantn.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "GiaoDichKhoaHocChiTiet")
public class GiaoDichKhoaHocChiTiet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dongia", precision = 12, scale = 2, nullable = false)
    private BigDecimal dongia;

    @ManyToOne
    @JoinColumn(name = "giaodichId", nullable = false)
    private GiaoDichKhoaHoc giaoDichKhoaHoc;

    @ManyToOne
    @JoinColumn(name = "khoahocId", nullable = false)
    private KhoaHoc khoahoc;
}
