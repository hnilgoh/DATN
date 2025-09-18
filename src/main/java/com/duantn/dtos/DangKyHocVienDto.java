package com.duantn.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DangKyHocVienDto {

    @Size(min = 2, max = 50, message = "Vui lòng nhập thông tin! ít nhất 2 ký tự.")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$", message = "Mật khẩu phải từ 6 đến 20 ký tự, có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!")
    private String password;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0")
    private String phone;
}
