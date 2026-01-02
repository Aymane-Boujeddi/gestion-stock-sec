package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;


    @OneToMany(mappedBy = "permission")
    private List<UserPermission> userPermissions ;

    @ManyToMany(mappedBy = "defaultPermissions")
    private List<RoleApp> roleApps ;
}
