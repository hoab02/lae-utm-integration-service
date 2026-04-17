package com.viettelpost.fms.utm_integration.infrastructure.kafka.support;

public final class KafkaTopicNames {

    public static final String SESSION_STATUS_UPDATES = "utm.session.status.changed";
    public static final String PILOT_REGISTRATION_RESULT_UPDATES = "utm.registry.pilot.updated";
    public static final String DRONE_REGISTRATION_RESULT_UPDATES = "utm.registry.drone.updated";
    public static final String FLIGHT_APPROVAL_RESULT_UPDATES = "utm.flight.approval.updated";
    public static final String NFZ_UPDATES = "utm.airspace.nfz.updated";
    public static final String CORRIDOR_UPDATES = "utm.airspace.corridor.updated";

    private KafkaTopicNames() {
    }
}
