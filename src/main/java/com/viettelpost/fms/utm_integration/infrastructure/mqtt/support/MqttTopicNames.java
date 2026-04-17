package com.viettelpost.fms.utm_integration.infrastructure.mqtt.support;

public final class MqttTopicNames {

    public static final String TELEMETRY_UPLINK = "utm/telemetry/uplink";
    public static final String COMMAND_DOWNLINK = "utm/command/downlink";
    public static final String COMMAND_ACK_UPLINK = "utm/command/ack/uplink";
    public static final String AIRSPACE_NFZ_UPDATES = "utm/airspace/nfz/updates";
    public static final String AIRSPACE_CORRIDOR_UPDATES = "utm/airspace/corridor/updates";
    public static final String FLIGHT_APPROVAL_STATUS_UPDATES = "utm/flight-approval/status/updates";

    private MqttTopicNames() {
    }
}
