package com.duantn.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaiKhoanUpdateDto {

    @Pattern(regexp = "^[\\p{L} .'-]{2,}$", message = "Họ tên phải có ít nhất 2 ký tự và chỉ chứa chữ cái cùng một số ký tự hợp lệ")
    private String name;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số")
    private String phone;
}
