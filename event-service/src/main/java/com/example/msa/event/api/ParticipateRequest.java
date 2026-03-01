package com.example.msa.event.api;

import jakarta.validation.constraints.NotNull;

public record ParticipateRequest(@NotNull Long memberId) {}
