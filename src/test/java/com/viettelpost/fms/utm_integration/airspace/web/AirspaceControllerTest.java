package com.viettelpost.fms.utm_integration.airspace.web;

import com.viettelpost.fms.common.i18n.I18nMessageService;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateStatus;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.service.AirspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AirspaceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AirspaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirspaceService airspaceService;

    @MockBean
    private I18nMessageService i18nMessageService;

    @Test
    void getLatestNfzShouldUseExpectedPath() throws Exception {
        when(airspaceService.getLatest(AirspaceUpdateType.NFZ)).thenReturn(Optional.of(AirspaceUpdateStatusDto.builder()
                .updateId("nfz-1")
                .type(AirspaceUpdateType.NFZ)
                .status(AirspaceUpdateStatus.RECEIVED)
                .build()));

        mockMvc.perform(get("/internal/utm/airspace/latest/nfz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.update_id").value("nfz-1"))
                .andExpect(jsonPath("$.type").value("NFZ"));
    }

    @Test
    void getLatestCorridorShouldUseExpectedPath() throws Exception {
        when(airspaceService.getLatest(AirspaceUpdateType.CORRIDOR)).thenReturn(Optional.of(AirspaceUpdateStatusDto.builder()
                .updateId("corridor-1")
                .type(AirspaceUpdateType.CORRIDOR)
                .status(AirspaceUpdateStatus.RECEIVED)
                .build()));

        mockMvc.perform(get("/internal/utm/airspace/latest/corridor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.update_id").value("corridor-1"))
                .andExpect(jsonPath("$.type").value("CORRIDOR"));
    }
}
