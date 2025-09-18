package com.duantn.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByEmail(String email);

    // Optional<TaiKhoan> findByName(String name);

    boolean existsByEmail(String email);

    List<TaiKhoan> findByRoleRoleId(Integer role);

    List<TaiKhoan> findByRole(Role role);

    @Query("SELECT tk FROM TaiKhoan tk " +
            "WHERE tk.role = :role " +
            "AND tk.id NOT IN (SELECT dh.taikhoan.id FROM DangHoc dh)")
    List<TaiKhoan> findHocVienChuaDangKy(@Param("role") Role role);

    @Query("SELECT DISTINCT dh.taikhoan FROM DangHoc dh")
    List<TaiKhoan> findTatCaNguoiDungDaDangKyHoc();

    //
    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.role.name = 'ROLE_GIANGVIEN'")
    List<TaiKhoan> findAllGiangVien();

    @Query("SELECT COUNT(tk) FROM TaiKhoan tk WHERE tk.role.name = 'ROLE_HOCVIEN'")
    int countHocVien();

    //
    TaiKhoan findByGiangVien_GiangvienId(Integer giangVienId);

    @Query("SELECT COUNT(tk) FROM TaiKhoan tk WHERE tk.role.name = 'ROLE_NHANVIEN'")
    int countNhanVien();

    List<TaiKhoan> findByRole_Name(String roleName);

    Page<TaiKhoan> findByRole(Role role, Pageable pageable);

    @Query("SELECT t.role.name, COUNT(t) FROM TaiKhoan t GROUP BY t.role.name")
    List<Object[]> countUsersByRole();

}
