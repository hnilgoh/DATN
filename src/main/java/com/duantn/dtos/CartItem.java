package com.duantn.dtos;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Integer khoaHocId;
    private String tenKhoaHoc;
    private BigDecimal gia;
    private BigDecimal giaGoc;
    private String anhBia;
    private String giangVien;

    private int soLuongDanhGia;
    private double diemTrungBinh;

}
