package com.famevently.monolith.event;

public class EventMapper {
    public static Event toEntity(CreateEventRequest request){
        Event event = new Event();
        event.setUserId(request.getUserId());
        event.setOverview(request.getEventOverview());
        event.setCategoryId(request.getEventCategoryId());
        event.setEventDate(request.getEventDate());
        event.setAddress(request.getEventAddress());
        event.setAdditionalInfo(request.getEventAdditionalInfo());
        event.setMinAge(request.getMinAge());
        event.setMaxAge(request.getMaxAge());
        event.setForAdultsOnly(request.getIsForAdultsOnly());
        return event;
    }
}
