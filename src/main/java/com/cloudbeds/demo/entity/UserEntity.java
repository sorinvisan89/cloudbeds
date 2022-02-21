package com.cloudbeds.demo.entity;

import com.cloudbeds.demo.converter.PasswordConverter;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "USERS")
public class UserEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    @Convert(converter = PasswordConverter.class)
    private String password;

    @ManyToMany
    @JoinTable(name = "USER_ADDRESSES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID"))
    private List<AddressEntity> addresses = new ArrayList<>();


}
