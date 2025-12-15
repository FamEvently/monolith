package com.famevently.monolith.event;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Event {
    private Long id;
    private Long userId;
    private String overview;
    private Long categoryId;
    private LocalDate eventDate;
    private String address;
    private String additionalInfo;
    private Integer minAge;
    private Integer maxAge;
    private Boolean forAdultsOnly;
    private OffsetDateTime createdAt;

    public Event() {}


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOverview() {
        return overview;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getAddress() {
        return address;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public Boolean getForAdultsOnly() {
        return forAdultsOnly;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public void setForAdultsOnly(Boolean forAdultsOnly) {
        this.forAdultsOnly = forAdultsOnly;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
