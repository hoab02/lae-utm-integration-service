package com.viettelpost.fms.utm_integration.approval.mapper;

import com.viettelpost.fms.utm_integration.approval.dto.api.UtmFlightApprovalRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightApprovalUtmMapper {

    public UtmFlightApprovalSubmitRequest toUtmSubmitRequest(UtmFlightApprovalRequest source) {
        UtmFlightApprovalSubmitRequest target = new UtmFlightApprovalSubmitRequest();

        target.setApplication(mapApplication(source.getApplication()));
        target.setReceivingAuthority(mapReceivingAuthority(source.getReceivingAuthority()));

        return target;
    }

    private UtmFlightApprovalSubmitRequest.Application mapApplication(UtmFlightApprovalRequest.Application source) {
        UtmFlightApprovalSubmitRequest.Application target = new UtmFlightApprovalSubmitRequest.Application();

        target.setAircrafts(mapAircrafts(source.getAircrafts()));
        target.setApplicant(mapApplicant(source.getApplicant()));
        target.setAttachments(mapAttachments(source.getAttachments()));
        target.setCommitment(mapCommitment(source.getCommitment()));
        target.setFlightPlan(mapFlightPlan(source.getFlightPlan()));
        target.setId(source.getId());
        target.setPilots(mapPilots(source.getPilots()));

        target.setCreatedAt(source.getCreatedAt() != null ? source.getCreatedAt() : Instant.now().toString());
        target.setUpdatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt() : Instant.now().toString());
        target.setStatus(source.getStatus() != null ? source.getStatus() : "SUBMITTED");

        return target;
    }

    private UtmFlightApprovalSubmitRequest.Aircrafts mapAircrafts(UtmFlightApprovalRequest.Aircrafts source) {
        UtmFlightApprovalSubmitRequest.Aircrafts target = new UtmFlightApprovalSubmitRequest.Aircrafts();
        target.setDroneId(source.getDroneId());
        return target;
    }

    private UtmFlightApprovalSubmitRequest.Applicant mapApplicant(UtmFlightApprovalRequest.Applicant source) {
        UtmFlightApprovalSubmitRequest.Applicant target = new UtmFlightApprovalSubmitRequest.Applicant();
        target.setAddress(source.getAddress());
        target.setApplicantType(source.getApplicantType());
        target.setDateOfBirth(source.getDateOfBirth());
        target.setEmail(source.getEmail());
        target.setFax(source.getFax());
        target.setIdNumber(source.getIdNumber());
        target.setLegalRepName(source.getLegalRepName());
        target.setNationality(source.getNationality());
        target.setOrganizationName(source.getOrganizationName());
        target.setPhone(source.getPhone());
        target.setTaxCode(source.getTaxCode());
        return target;
    }

    private List<UtmFlightApprovalSubmitRequest.Attachment> mapAttachments(List<UtmFlightApprovalRequest.Attachment> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream().map(item -> {
            UtmFlightApprovalSubmitRequest.Attachment target = new UtmFlightApprovalSubmitRequest.Attachment();
            target.setFileName(item.getFileName());
            target.setFileUrl(item.getFileUrl());
            target.setId(item.getId());
            target.setType(item.getType());
            return target;
        }).collect(Collectors.toList());
    }

    private UtmFlightApprovalSubmitRequest.Commitment mapCommitment(UtmFlightApprovalRequest.Commitment source) {
        UtmFlightApprovalSubmitRequest.Commitment target = new UtmFlightApprovalSubmitRequest.Commitment();
        target.setAgreed(source.getAgreed());
        target.setDate(source.getDate());
        return target;
    }

    private UtmFlightApprovalSubmitRequest.FlightPlan mapFlightPlan(UtmFlightApprovalRequest.FlightPlan source) {
        UtmFlightApprovalSubmitRequest.FlightPlan target = new UtmFlightApprovalSubmitRequest.FlightPlan();
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setOperationArea(mapOperationArea(source.getOperationArea()));
        target.setPurpose(mapPurpose(source.getPurpose()));
        target.setTakeoffLandingSites(mapTakeoffLandingSites(source.getTakeoffLandingSites()));
        return target;
    }

    private UtmFlightApprovalSubmitRequest.OperationArea mapOperationArea(UtmFlightApprovalRequest.OperationArea source) {
        UtmFlightApprovalSubmitRequest.OperationArea target = new UtmFlightApprovalSubmitRequest.OperationArea();
        target.setAltitudeMaxM(source.getAltitudeMaxM());
        target.setAltitudeMinM(source.getAltitudeMinM());
        target.setCommune(source.getCommune());
        target.setDistrict(source.getDistrict());
        target.setName(source.getName());
        target.setProvince(source.getProvince());

        if (source.getCoordinates() != null) {
            target.setCoordinates(source.getCoordinates().stream().map(item -> {
                UtmFlightApprovalSubmitRequest.Coordinate coordinate = new UtmFlightApprovalSubmitRequest.Coordinate();
                coordinate.setLat(item.getLat());
                coordinate.setLng(item.getLng());
                return coordinate;
            }).collect(Collectors.toList()));
        } else {
            target.setCoordinates(Collections.emptyList());
        }

        return target;
    }

    private UtmFlightApprovalSubmitRequest.Purpose mapPurpose(UtmFlightApprovalRequest.Purpose source) {
        UtmFlightApprovalSubmitRequest.Purpose target = new UtmFlightApprovalSubmitRequest.Purpose();
        target.setDescription(source.getDescription());
        target.setPurposeCode(source.getPurposeCode());
        return target;
    }

    private List<UtmFlightApprovalSubmitRequest.TakeoffLandingSite> mapTakeoffLandingSites(
            List<UtmFlightApprovalRequest.TakeoffLandingSite> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream().map(item -> {
            UtmFlightApprovalSubmitRequest.TakeoffLandingSite target =
                    new UtmFlightApprovalSubmitRequest.TakeoffLandingSite();
            target.setDescription(item.getDescription());
            target.setName(item.getName());
            target.setType(item.getType());

            if (item.getLocation() != null) {
                UtmFlightApprovalSubmitRequest.Location location = new UtmFlightApprovalSubmitRequest.Location();
                location.setLat(item.getLocation().getLat());
                location.setLng(item.getLocation().getLng());
                target.setLocation(location);
            }

            return target;
        }).collect(Collectors.toList());
    }

    private List<UtmFlightApprovalSubmitRequest.Pilot> mapPilots(List<UtmFlightApprovalRequest.Pilot> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream().map(item -> {
            UtmFlightApprovalSubmitRequest.Pilot target = new UtmFlightApprovalSubmitRequest.Pilot();
            target.setFullName(item.getFullName());
            target.setIdNumber(item.getIdNumber());
            target.setLicenseNo(item.getLicenseNo());
            target.setPhone(item.getPhone());
            target.setDateOfBirth(item.getDateOfBirth());
            target.setLicenseIssueDate(item.getLicenseIssueDate());
            return target;
        }).collect(Collectors.toList());
    }


    private UtmFlightApprovalSubmitRequest.ReceivingAuthority mapReceivingAuthority(
            UtmFlightApprovalRequest.ReceivingAuthority source) {
        UtmFlightApprovalSubmitRequest.ReceivingAuthority target =
                new UtmFlightApprovalSubmitRequest.ReceivingAuthority();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setReceivingAuthorityId(source.getReceivingAuthorityId());
        target.setReceivingAuthorityName(source.getReceivingAuthorityName());
        return target;
    }
}