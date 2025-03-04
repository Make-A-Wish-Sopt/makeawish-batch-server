package com.makeawishbatchserver.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tmp_birthday")
public class TmpBirthday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "birth_date")
    private String birthDate;
    @Column(name = "phone_number")
    private String phoneNumber;
}

