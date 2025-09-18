package com.duantn.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "GioHang")
public class GioHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer giohangId;

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;

    @OneToOne
    @JoinColumn(name = "taikhoanId", unique = true, nullable = false)
    private TaiKhoan taikhoan;
}
