package com.api.gestion.apigestionfacturas.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "User.findByEmail", query = "select u from UserModel u where u.email=:email")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(name = "name")
    private String name;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;
}
