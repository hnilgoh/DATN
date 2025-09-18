package com.duantn.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "NguoiDungThichKhoaHoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungThichKhoaHoc implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taikhoan_id", nullable = false)
    private TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoahoc_id", nullable = false)
    private KhoaHoc khoaHoc;
}
