package com.example.msa.member.api;

import com.example.msa.member.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberRequest(
        @NotBlank String name,
        @Email String email,
        String birthday
) {
    public Member toEntity() {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        if (birthday != null && !birthday.isBlank()) {
            member.setBirthday(LocalDate.parse(birthday));
        }
        member.setCreatedAt(LocalDateTime.now());
        return member;
    }
}
