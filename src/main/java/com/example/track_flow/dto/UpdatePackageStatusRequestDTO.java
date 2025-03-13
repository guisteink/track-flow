package com.example.track_flow.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePackageStatusRequestDTO {

    @NotBlank(message = "Status is mandatory")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}