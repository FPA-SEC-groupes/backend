package com.HelloWay.HelloWay.services;


import com.HelloWay.HelloWay.entities.Event;
import com.HelloWay.HelloWay.entities.Party;
import com.HelloWay.HelloWay.entities.Promotion;
import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.payload.request.EventDTO;
import com.HelloWay.HelloWay.repos.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository ;

    @Autowired
    SpaceService spaceService ;

    public Event createEvent(Event event){
        return eventRepository.save(event);
    }
    
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public Event updateEvent(Event updatedEvent) {
        Event existingEvent = eventRepository.findById(updatedEvent.getIdEvent()).orElse(null);
        if (existingEvent != null) {
            // Copy the properties from the updatedEvent to the existingEvent
            existingEvent.setEventTitle(updatedEvent.getEventTitle());
            existingEvent.setStartDate(updatedEvent.getStartDate());
            existingEvent.setEndDate(updatedEvent.getEndDate());
            existingEvent.setDescription(updatedEvent.getDescription());
    
            // Check if the event is a Party and update specific attributes
    
            return eventRepository.save(existingEvent);
        } else {
            throw new ResourceNotFoundException("Event not found");
        }
    }
    
    public Event updateDetailsEvent(EventDTO updatedEvent) {
        Party existingEvent = eventRepository.findPartyById(updatedEvent.getIdEvent()).orElse(null);
        if (existingEvent != null) {
            // Copy the properties from the updatedEvent to the existingEvent
            existingEvent.setEventTitle(updatedEvent.getEventTitle());
            existingEvent.setStartDate(updatedEvent.getStartDate());
            existingEvent.setEndDate(updatedEvent.getEndDate());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setNbParticipant(updatedEvent.getNbParticipant());
            existingEvent.setPrice(updatedEvent.getPrice());
            existingEvent.setAllInclusive(updatedEvent.getAllInclusive());
            return eventRepository.save(existingEvent);
        } else {
            throw new ResourceNotFoundException("Event not found");
        }
    }

    public Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElse(null);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Promotion createPromotion(Promotion promotion) {
        return eventRepository.save(promotion);
    }

    public Party createParty(Party party) {
        return eventRepository.save(party);
    }

    public List<Promotion> getAllPromotions() {
        return eventRepository.findAllPromotions();
    }

    public List<Party> getAllParties() {
        return eventRepository.findAllParties();
    }

    public Event updateEvent(Long eventId, Event updatedEvent) {
        Event event = findEventById(eventId);
        event.setEventTitle(updatedEvent.getEventTitle());
        event.setDescription(updatedEvent.getDescription());
        // Update other properties as needed
        return eventRepository.save(event);
    }

    public List<Event> getEventsBySpaceId(Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        return eventRepository.findBySpace(space);
    }

    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByStartDateBetween(startDate, endDate);
    }

    public List<Event> getUpcomingEvents(int limit) {
        LocalDate currentDate = LocalDate.now();
        return eventRepository.findUpcomingEvents(currentDate, PageRequest.of(0, limit));
    }

}
