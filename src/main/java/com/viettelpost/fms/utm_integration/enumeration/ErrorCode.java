package com.viettelpost.fms.utm_integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ERROR_GENERAL("error.general"),
    ERROR_REQUEST_INVALID("error.request.invalid"),
    ERROR_PERMISSION_DENIED("error.permission"),
    ERROR_SESSION_ALREADY_CONNECTED("error.session.already.connected"),
    ERROR_SESSION_NOT_CONNECTED("error.session.not.connected"),
    ERROR_APPROVAL_NOT_FOUND("error.approval.not.found"),
    ERROR_APPROVAL_NOT_APPROVED("error.approval.not.approved"),
    ERROR_MISSION_TRANSITION_INVALID("error.mission.transition.invalid"),
    ERROR_PILOT_REGISTRATION_NOT_FOUND("error.pilot.registration.not.found"),
    ERROR_DRONE_REGISTRATION_NOT_FOUND("error.drone.registration.not.found"),
    ERROR_REGISTRATION_ALREADY_SUBMITTED("error.registration.already.submitted");

    private final String i18Key;
}
