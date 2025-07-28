package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import com.lovedbug.geulgwi.core.domain.member.dto.UpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(name = "profile_url")
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MemberGender gender;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @PrePersist
    protected void onCreate(){
        this.status = MemberStatus.ACTIVE;
        this.role = MemberRole.USER;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return status == MemberStatus.ACTIVE;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return status != MemberStatus.SUSPENDED;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status == MemberStatus.ACTIVE;
    }

    public void changeNickname(String newNickname){
        this.nickname = newNickname;
    }

    public void changePassword(String hashedPassword){
        this.password = hashedPassword;
    }

    public void changeStatus(MemberStatus newStatus){
        this.status = newStatus;
    }

    public void changeBirthDate(LocalDateTime birthDate){
        this.birthDate = birthDate;
    }

    public void changeGender(MemberGender gender){
        this.gender = gender;
    }

    public void updateFromRequest(UpdateRequestDto updateRequest, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        updateNickname(updateRequest.getNickname(), memberRepository);
        updatePassword(updateRequest.getPassword(), passwordEncoder);
        updateBirthDate(updateRequest.getBirthDate());
        updateGender(updateRequest.getGender());
    }

    private void updateNickname(String newNickname, MemberRepository memberRepository) {
        if (isValidNewValue(newNickname) && !newNickname.equals(this.nickname)) {
            validateNickname(newNickname, memberRepository);
            changeNickname(newNickname);
        }
    }

    private void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        if (isValidNewValue(newPassword)) {
            changePassword(passwordEncoder.encode(newPassword));
        }
    }

    private void updateBirthDate(LocalDateTime newBirthDate) {
        if (newBirthDate != null) {
            changeBirthDate(newBirthDate);
        }
    }

    private void updateGender(MemberGender newGender) {
        if (newGender != null) {
            changeGender(newGender);
        }
    }

    private boolean isValidNewValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void validateNickname(String newNickname, MemberRepository memberRepository) {
        if (memberRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다. nickname = " + newNickname);
        }
    }

}
