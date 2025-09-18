package com.duantn.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "KetQuaChiTiet")
public class KetQuaChiTiet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_ChiTiet")
    private Integer id;

    @Column(name = "dapanchon", nullable = false)
    private Integer dapAnChon;

    @Column(name = "dunghaysai")
    private Boolean dungHaySai;

    @Column(name = "diem", nullable = false)
    private Double diem;

    @ManyToOne
    @JoinColumn(name = "ketquaId", nullable = false)
    private KetQua ketQua;

    @ManyToOne
    @JoinColumn(name = "cauHoiId", nullable = false)
    private CauHoi cauHoi;

    @ManyToOne
    @JoinColumn(name = "dapanId", nullable = false)
    private DapAn dapAn;

}
