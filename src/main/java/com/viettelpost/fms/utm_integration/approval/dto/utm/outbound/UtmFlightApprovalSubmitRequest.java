package com.viettelpost.fms.utm_integration.approval.dto.utm.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UtmFlightApprovalSubmitRequest {

    private Application application;
    private ReceivingAuthority receivingAuthority;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Application {
        private Aircrafts aircrafts;
        private Applicant applicant;
        private List<Attachment> attachments;
        private Commitment commitment;
        private String createdAt;
        private FlightPlan flightPlan;
        private String id;
        private List<Pilot> pilots;
        private String status;
        private String updatedAt;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Aircrafts {
        private List<String> droneId;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Applicant {
        private String address;
        private String applicantType;
        private String dateOfBirth;
        private String email;
        private String fax;
        private String idNumber;
        private String legalRepName;
        private String nationality;
        private String organizationName;
        private String phone;
        private String taxCode;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Attachment {
        private String fileName;
        private String fileUrl;
        private String id;
        private String type;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Commitment {
        private Boolean agreed;
        private String date;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class FlightPlan {
        private String endTime;
        private OperationArea operationArea;
        private Purpose purpose;
        private String startTime;
        private List<TakeoffLandingSite> takeoffLandingSites;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OperationArea {
        private Integer altitudeMaxM;
        private Integer altitudeMinM;
        private String commune;
        private List<Coordinate> coordinates;
        private String district;
        private String name;
        private String province;
    }

    @Data
    public static class Coordinate {
        private Double lat;
        private Double lng;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Purpose {
        private String description;
        private String purposeCode;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TakeoffLandingSite {
        private String description;
        private Location location;
        private String name;
        private String type;
    }

    @Data
    public static class Location {
        private Double lat;
        private Double lng;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Pilot {
        private String fullName;
        private String idNumber;
        private String licenseNo;
        private String phone;

        private String dateOfBirth;
        private String licenseIssueDate;

//        @JsonProperty("date_of_birth")
//        private TimeWrapper dateOfBirth;
//
//        @JsonProperty("license_issue_date")
//        private TimeWrapper licenseIssueDate;

    }
//
//    @Data
//    public static class TimeWrapper {
//        @JsonProperty("time.Time")
//        private String time;
//    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ReceivingAuthority {
        private String id;
        private String name;
        private String receivingAuthorityId;
        private String receivingAuthorityName;
    }
}