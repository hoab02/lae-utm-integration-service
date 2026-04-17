package com.viettelpost.fms.utm_integration.approval.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.utm.approval")
public class ApprovalProperties {

    /**
     * Path REST submit approval sang UTM.
     * Chỉ giữ phần path riêng của approval, còn host dùng integration.utm.base-url
     */
    private String submitPath = "/api/v1/flight-approvals";
}