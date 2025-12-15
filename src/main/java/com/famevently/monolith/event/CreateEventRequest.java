package com.famevently.monolith.event;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class CreateEventRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String eventOverview;

    @NotNull
    private Long eventCategoryId;

    @NotNull
    @Future
    private LocalDate eventDate;

    @NotBlank
    @Size(max = 500)
    private String eventAddress;

    private String eventAdditionalInfo;

    @Min(0)
    @Max(120)
    private Integer minAge;

    @Min(0)
    @Max(120)
    private Integer maxAge;

    private Boolean isForAdultsOnly;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventOverview() {
        return eventOverview;
    }

    public void setEventOverview(String eventOverview) {
        this.eventOverview = eventOverview;
    }

    public Long getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(Long eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventAdditionalInfo() {
        return eventAdditionalInfo;
    }

    public void setEventAdditionalInfo(String eventAdditionalInfo) {
        this.eventAdditionalInfo = eventAdditionalInfo;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Boolean getIsForAdultsOnly() {
        return isForAdultsOnly;
    }

    public void setIsForAdultsOnly(Boolean isForAdultsOnly) {
        this.isForAdultsOnly = isForAdultsOnly;
    }
}
