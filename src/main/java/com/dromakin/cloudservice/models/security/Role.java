/*
 * File:     Role
 * Package:  com.dromakin.cloudservice.models
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.models.security;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles", schema = "public")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

}
