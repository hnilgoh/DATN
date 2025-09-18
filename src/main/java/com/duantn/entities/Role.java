package com.duantn.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private Integer roleId;

    @Column(name = "name", columnDefinition = "NVARCHAR(MAX)")
    private String name;

    // Quan hệ 1-n với bảng TaiKhoan
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<TaiKhoan> taikhoans;

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                '}';
    }
}
