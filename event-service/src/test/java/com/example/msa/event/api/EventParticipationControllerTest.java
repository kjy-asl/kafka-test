package com.example.msa.event.api;

import com.example.msa.event.service.EventParticipationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventParticipationController.class)
class EventParticipationControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean EventParticipationService participationService;

    @Test
    void participate_성공_200() throws Exception {
        when(participationService.participate(1L, 42L)).thenReturn(true);

        mockMvc.perform(post("/events/1/participate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": 42}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTICIPATED"));
    }

    @Test
    void participate_정원초과_409() throws Exception {
        when(participationService.participate(1L, 42L)).thenReturn(false);

        mockMvc.perform(post("/events/1/participate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": 42}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("FULL"));
    }

    @Test
    void participate_이벤트없음_404() throws Exception {
        when(participationService.participate(99L, 42L))
                .thenThrow(new EntityNotFoundException("Event not found: 99"));

        mockMvc.perform(post("/events/99/participate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": 42}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_memberId_누락_400() throws Exception {
        mockMvc.perform(post("/events/1/participate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
