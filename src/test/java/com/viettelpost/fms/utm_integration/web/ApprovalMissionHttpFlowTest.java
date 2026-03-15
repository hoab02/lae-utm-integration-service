package com.viettelpost.fms.utm_integration.web;

import com.viettelpost.fms.common.i18n.I18nMessageService;
import com.viettelpost.fms.utm_integration.approval.client.StubUtmApprovalClient;
import com.viettelpost.fms.utm_integration.approval.domain.FlightApprovalEntity;
import com.viettelpost.fms.utm_integration.approval.repository.FlightApprovalRepository;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalServiceImpl;
import com.viettelpost.fms.utm_integration.approval.web.FlightApprovalController;
import com.viettelpost.fms.utm_integration.approval.web.FlightApprovalDevController;
import com.viettelpost.fms.utm_integration.mission.client.StubUtmMissionClient;
import com.viettelpost.fms.utm_integration.mission.domain.MissionEntity;
import com.viettelpost.fms.utm_integration.mission.repository.MissionRepository;
import com.viettelpost.fms.utm_integration.mission.service.MissionServiceImpl;
import com.viettelpost.fms.utm_integration.mission.web.MissionController;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.service.DroneRegistrationService;
import com.viettelpost.fms.utm_integration.registry.service.PilotRegistrationService;
import com.viettelpost.fms.utm_integration.session.client.StubUtmSessionClient;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.repository.UtmSessionRepository;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionServiceImpl;
import com.viettelpost.fms.utm_integration.session.web.UtmSessionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UtmSessionController.class,
        FlightApprovalController.class,
        FlightApprovalDevController.class,
        MissionController.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "utm.approval.dev-update-enabled=true",
        "server.servlet.context-path="
})
@Import({
        UtmSessionServiceImpl.class,
        FlightApprovalServiceImpl.class,
        MissionServiceImpl.class,
        StubUtmSessionClient.class,
        StubUtmApprovalClient.class,
        StubUtmMissionClient.class
})
class ApprovalMissionHttpFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private I18nMessageService i18nMessageService;

    @MockBean
    private UtmSessionRepository utmSessionRepository;

    @MockBean
    private FlightApprovalRepository flightApprovalRepository;

    @MockBean
    private MissionRepository missionRepository;

    @MockBean
    private PilotRegistrationService pilotRegistrationService;

    @MockBean
    private DroneRegistrationService droneRegistrationService;

    private final AtomicReference<UtmSessionEntity> storedSession = new AtomicReference<>();
    private final AtomicReference<FlightApprovalEntity> storedApproval = new AtomicReference<>();
    private final AtomicReference<MissionEntity> storedMission = new AtomicReference<>();

    @BeforeEach
    void setUp() throws Exception {
        storedSession.set(null);
        storedApproval.set(null);
        storedMission.set(null);

        when(utmSessionRepository.findTopByOrderByCreatedDateDesc())
                .thenAnswer(invocation -> Optional.ofNullable(storedSession.get()));
        when(utmSessionRepository.save(any(UtmSessionEntity.class))).thenAnswer(invocation -> {
            UtmSessionEntity session = invocation.getArgument(0);
            storedSession.set(session);
            return session;
        });

        when(flightApprovalRepository.findByPlanId(any(String.class))).thenAnswer(invocation -> {
            String planId = invocation.getArgument(0);
            FlightApprovalEntity approval = storedApproval.get();
            if (approval == null || !planId.equals(approval.getPlanId())) {
                return Optional.empty();
            }
            return Optional.of(approval);
        });
        when(flightApprovalRepository.save(any(FlightApprovalEntity.class))).thenAnswer(invocation -> {
            FlightApprovalEntity approval = invocation.getArgument(0);
            storedApproval.set(approval);
            return approval;
        });

        when(missionRepository.findByMissionId(any(String.class))).thenAnswer(invocation -> {
            String missionId = invocation.getArgument(0);
            MissionEntity mission = storedMission.get();
            if (mission == null || !missionId.equals(mission.getMissionId())) {
                return Optional.empty();
            }
            return Optional.of(mission);
        });
        when(missionRepository.save(any(MissionEntity.class))).thenAnswer(invocation -> {
            MissionEntity mission = invocation.getArgument(0);
            storedMission.set(mission);
            return mission;
        });

        when(pilotRegistrationService.getByPilotId("pilot-1")).thenReturn(PilotRegistrationStatusDto.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.APPROVED)
                .build());
        when(droneRegistrationService.getByDroneId("drone-1")).thenReturn(DroneRegistrationStatusDto.builder()
                .droneId("drone-1")
                .status(RegistrationStatus.APPROVED)
                .build());
    }

    @Test
    void submitApproveAndAirborneShouldSucceedOverHttpInDevProfile() throws Exception {
        mockMvc.perform(post("/internal/utm/session/connect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONNECTED"));

        mockMvc.perform(post("/internal/utm/flight-approvals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plan_id": "plan-1",
                                  "mission_id": "mission-1",
                                  "drone_id": "drone-1",
                                  "pilot_id": "pilot-1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.plan_id").value("plan-1"));

        mockMvc.perform(post("/internal/utm/dev/flight-approvals/plan-1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approved_at").isNotEmpty());

        mockMvc.perform(post("/internal/utm/missions/airborne")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mission_id": "mission-1",
                                  "plan_id": "plan-1",
                                  "drone_id": "drone-1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("AIRBORNE"))
                .andExpect(jsonPath("$.mission_id").value("mission-1"))
                .andExpect(jsonPath("$.plan_id").value("plan-1"))
                .andExpect(jsonPath("$.airborne_at").isNotEmpty());
    }
}
