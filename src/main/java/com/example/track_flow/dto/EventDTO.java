package com.example.track_flow.dto;

public class EventDTO {
    private String pkgId;
    private String localization;
    private String description;
    private String timestamp;

    public EventDTO(String pkgId, String localization, String description, String timestamp) {
        this.pkgId = pkgId;
        this.localization = localization;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getPkgId() {
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}