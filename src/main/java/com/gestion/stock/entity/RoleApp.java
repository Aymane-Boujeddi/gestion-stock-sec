package com.gestion.stock.entity;

import com.gestion.stock.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "role_apps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 50)
    private Role name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_default_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> defaultPermissions;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users ;
}
