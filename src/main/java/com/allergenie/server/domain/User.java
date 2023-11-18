package com.allergenie.server.domain;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Setter
@Where(clause = "delete_flag=0")
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column
    @NotNull
    private String nickname;

    @Column
    @NotNull
    private String email;

    @Column
    @NotNull
    private String password;

    @Column
    @NotNull
    private Long imageId;


    @Column
    private String role;

    @Column(name="delete_flag")
    @NotNull
    private Boolean deleteFlag;

    //User 클래스의 생성자 역할
    @Builder
    public User( String nickname, String email, String password, Long imageId) {
        this.nickname = nickname;
        if(email!=null) this.email = email;
        this.password = password;
        this.deleteFlag = false;
        this.imageId = imageId;
        role = "USER";
    }
}
