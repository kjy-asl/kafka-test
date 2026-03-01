package com.example.msa.event.api;

public record ParticipateResponse(String status) {

    public static ParticipateResponse participated() {
        return new ParticipateResponse("PARTICIPATED");
    }

    public static ParticipateResponse full() {
        return new ParticipateResponse("FULL");
    }
}
