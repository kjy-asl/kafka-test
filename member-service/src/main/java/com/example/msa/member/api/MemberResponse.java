package com.example.msa.member.api;

import com.example.msa.member.domain.Member;

public record MemberResponse(Long id, String name, String email, String birthday) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getBirthday() != null ? member.getBirthday().toString() : null
        );
    }
}
