package com.duantn.serviceImpl;

import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanServiceImpl implements TaiKhoanService {

    @Autowired
    private TaiKhoanRepository repository;

    @Override
    public List<TaiKhoan> layTatCaNhanVien() {
        return repository.findByRoleRoleId(2); // Role ID = 2 là nhân viên
    }

    @Override
    public TaiKhoan themNhanVien(TaiKhoan taiKhoan) {
        Role nhanVienRole = new Role();
        nhanVienRole.setRoleId(2);
        taiKhoan.setRole(nhanVienRole);
        return repository.save(taiKhoan);
    }

    @Override
    public TaiKhoan capNhatNhanVien(Integer id, TaiKhoan taiKhoanMoi) {
        TaiKhoan nv = layTheoId(id);
        nv.setName(taiKhoanMoi.getName());
        nv.setEmail(taiKhoanMoi.getEmail());
        nv.setPhone(taiKhoanMoi.getPhone());
        nv.setPassword(taiKhoanMoi.getPassword());
        return repository.save(nv);
    }

    @Override
    public void xoaNhanVien(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public TaiKhoan layTheoId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    @Override
    public TaiKhoan register(TaiKhoan taiKhoan) {
        // Kiểm tra email đã tồn tại chưa
        if (repository.findByEmail(taiKhoan.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }
        return repository.save(taiKhoan);
    }

    @Override
    public TaiKhoan login(String email, String password) {
        // Tìm tài khoản theo email
        TaiKhoan tk = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        // So sánh mật khẩu (ở đây là plain text, bạn nên dùng BCrypt để an toàn hơn)
        if (!tk.getPassword().equals(password)) {
            throw new RuntimeException("Sai mật khẩu");
        }

        return tk;
    }

    @Override
    public TaiKhoan getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // hoặc username
        return repository.findByEmail(email).orElseThrow();
    }

    @Override
    public TaiKhoan findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Override
    public Optional<TaiKhoan> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public TaiKhoan findByUsername(String username) {
        return repository.findByEmail(username).orElse(null);
    }

    //
    @Override
    public List<TaiKhoan> layTatCaGiangVien() {
        return repository.findAllGiangVien();
    }

}
