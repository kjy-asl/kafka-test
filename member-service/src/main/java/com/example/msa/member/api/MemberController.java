package com.example.msa.member.api;

import com.example.msa.member.domain.Member;
import com.example.msa.member.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Validated @RequestBody MemberRequest request) {
        Member member = memberRepository.save(request.toEntity());
        return ResponseEntity.created(URI.create("/members/" + member.getId()))
                .body(MemberResponse.from(member));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(MemberResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
