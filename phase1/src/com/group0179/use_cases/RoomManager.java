package com.group0179.use_cases;

import com.group0179.entities.Event;
import com.group0179.entities.Room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * Create rooms
 * Store rooms in a map by UUID
 * Get rooms by UUID
 * <p>
 * Create events
 * Store events in existing rooms
 * Get events from rooms by UUID
 * Get all events from room
 * <p>
 * Reschedule an event
 * <p>
 * Sign up an attendee for an Event
 * Remove an attendee from an Event
 *
 * @author Justin Chan
 */

public class RoomManager implements Serializable {
    private final ArrayList<Room> rooms = new ArrayList<>();

    public RoomManager() {
    }

    /**
     * @param roomID the UUID of the room
     * @return the room corresponding to the UUID
     * <p>
     * Precondition: the roomID is a key in rooms
     */
    private Room getRoom(UUID roomID) {
        return getRoomIDToRoom().get(roomID);
    }

    private Room getRoom(int roomNumber) {
        return getRooms().get(roomNumber);
    }

    private Room getEventRoom(Event event) {
        for (Room room : rooms) {
            for (Event comparisonEvent : room.getEvents()) {
                if (event.equals(comparisonEvent)) {
                    return room;
                }
            }
        }
        return new Room();
    }

    /**
     * @param eventID the eventID of the event
     * @return the event corresponding to the eventID
     * <p>
     * Precondition: event must exist in one of the rooms
     */
    private Event getEvent(UUID eventID) {
        return getEventIDToEvent().get(eventID);
    }

    private Event getEvent(int roomNumber, int eventNumber) {
        return getEventsFromRoom(roomNumber).get(eventNumber);
    }

    private Event getEventFromRoom(int roomNumber, int eventNumber) {
        return getEventsFromRoom(roomNumber).get(eventNumber);
    }

    /**
     * @return an ArrayList of all rooms in existence
     */
    private ArrayList<Room> getRooms() {
        return rooms;
    }

    private ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<>();
        for (Room room : rooms) {
            events.addAll(room.getEvents());
        }
        return events;
    }

    /**
     * @param room the room that contains the desired events
     * @return an ArrayList of Events in the Room
     */
    private ArrayList<Event> getEventsFromRoom(Room room) {
        return room.getEvents();
    }

    private ArrayList<Event> getEventsFromRoom(int roomNumber) {
        return getEventsFromRoom(getRooms().get(roomNumber));
    }

    private ArrayList<Event> eventIDsToEvents(ArrayList<UUID> eventIDs) {
        ArrayList<Event> events = new ArrayList<>();
        for (UUID eventID : eventIDs) {
            events.add(getEvent(eventID));
        }
        return events;
    }

    /**
     * @param eventID the event storing the attendees
     * @return an ArrayList of AttendeeIDs that are attending the specified event in the specified room
     * <p>
     * Precondition: roomID must exist and event must exist within the corresponding room
     */
    public ArrayList<UUID> getEventAttendeeIDs(UUID eventID) {
        Event event = getEvent(eventID);
        return event.getAttendeeIDs();
    }

    private HashMap<UUID, Room> getRoomIDToRoom() {
        HashMap<UUID, Room> RoomIDToRoom = new HashMap<>();
        for (Room room : rooms) {
            RoomIDToRoom.put(room.getRoomID(), room);
        }
        return RoomIDToRoom;
    }

    private HashMap<UUID, Event> getEventIDToEvent() {
        HashMap<UUID, Event> EventIDToEvent = new HashMap<>();
        for (Room room : rooms) {
            for (Event event : room.getEvents()) {
                EventIDToEvent.put(event.getEventID(), event);
            }
        }
        return EventIDToEvent;
    }

    public int getNumRooms() {
        return rooms.size();
    }

    public int getNumEvents() {
        return getEvents().size();
    }

    public int getNumEventsInRoom(int roomNumber) {
        return getEventsFromRoom(roomNumber).size();
    }

    public ArrayList<UUID> getEventIDs() {
        ArrayList<UUID> eventIDs = new ArrayList<>();
        for (Event event : getEvents()) {
            eventIDs.add(event.getEventID());
        }
        return eventIDs;
    }

    public void newRoom() {
        Room roomToCreate = new Room();
        rooms.add(roomToCreate);
    }

    /**
     * @param eventTitle  the title of the event
     * @param speakerName the speaker of the event
     * @param startTime   the start time of the event
     * @param endTime     the end time of the event
     * @param roomNumber  the room of the event
     * @return true if the event can be added and false if the event cannot be added. An event may not be added if
     * the time is out of bounds, is overlapping another event in the same room, or the speaker is giving a talk
     * at the same time.
     * <p>
     * Preconditions: room exists
     */
    public boolean newEventValid(String eventTitle, String speakerName, Calendar startTime, Calendar endTime, int roomNumber, UserManager um) {
        Room room = getRoom(roomNumber);

        Event newEvent = new Event(eventTitle, speakerName, startTime, endTime);
        for (UUID existingEventID : um.getSpeakerEventIDs(speakerName)) {
            if (room.eventOverlapping(newEvent, getEvent(existingEventID))) { // TODO: Method does not require room!
                return false;
            }
        }
        return room.eventIsValid(newEvent);
    }

    public boolean newEventValid(String eventTitle, String speakerName, Calendar startTime, Calendar endTime, Room room, UserManager um) {
        Event newEvent = new Event(eventTitle, speakerName, startTime, endTime);
        for (UUID existingEventID : um.getSpeakerEventIDs(speakerName)) {
            if (room.eventOverlapping(newEvent, getEvent(existingEventID))) {
                return false;
            }
        }
        return room.eventIsValid(newEvent);
    }

    /**
     * @param eventTitle  the title of the event
     * @param speakerName the speaker of the event
     * @param startTime   the start time of the event
     * @param endTime     the end time of the event
     * @param roomNumber  the roomNumber of the room that the event should be added to
     * @return the UUID of the created event
     * <p>
     * This method adds the newEvent to the specified roomNumber and the specified speakerName.
     * Precondition: the event can be added to the roomNumber without conflict and the roomNumber exists
     */
    public UUID newEvent(String eventTitle, String speakerName, Calendar startTime, Calendar endTime, int roomNumber, UserManager um) {
        Room room = getRoom(roomNumber);
        Event newEvent = new Event(eventTitle, speakerName, startTime, endTime);
        room.addEvent(newEvent);
        um.speakerAddEvent(speakerName, room.getRoomID(), newEvent.getEventID());
        return newEvent.getEventID();
    }

    /**
     * @param roomNumber  the room that the event is in
     * @param eventNumber the event to be rescheduled
     * @param startTime   the new start time
     * @param endTime     the new end time
     * @return true if the event could be rescheduled and false if no changes were made due to failed reschedule
     */
    public boolean rescheduleEvent(UserManager um, int roomNumber, int eventNumber, Calendar startTime, Calendar endTime) {
        Event event = getEventFromRoom(roomNumber, eventNumber);
        Room room = getRoom(roomNumber);

        if (newEventValid(event.getTitle(), event.getSpeakerName(), startTime, endTime, room, um)) {
            event.setTime(startTime, endTime);
            return true;
        }
        return false;
    }

    /**
     * Removes a desired Event from the list of events and all participants
     *
     * @param eventNumber the event to be removed
     * @return true if the event was removed or false if there was no such event in the schedule
     */
    public boolean removeEvent(UserManager um, int roomNumber, int eventNumber) {
        Event event = getEvent(roomNumber, eventNumber);

        Room room = getEventRoom(event);
        for (UUID attendeeID : event.getAttendeeIDs()) {
            um.attendeeRemoveEvent(attendeeID, room.getRoomID(), event.getEventID());
        }

        um.speakerRemoveEvent(event.getSpeakerName(), room.getRoomID(), event.getEventID());
        return room.removeEvent(event);
    }

    /**
     * @param attendeeID the attendee that is applying
     * @param eventID    the eventID being applied for
     * @return true if the sign up was successful, or false if the attendee could not sign up (as they're already signed up)
     */
    public boolean addEventAttendee(UUID attendeeID, UUID eventID, UserManager um) {
        Event event = getEvent(eventID);

        if (event.getAttendeeIDs().size() >= 2) return false;

        if (event.getAttendeeIDs().contains(attendeeID)) {
            return false;
        }
        um.attendeeAddEvent(attendeeID, getEventRoom(event).getRoomID(), eventID);
        event.addAttendee(attendeeID);
        return true;
    }

    public boolean addEventAttendee(UUID attendeeID, int roomNumber, int eventNumber, UserManager um) {
        Event event = getEvent(roomNumber, eventNumber);

        if (event.getAttendeeIDs().size() >= 2) return false;

        if (event.getAttendeeIDs().contains(attendeeID)) {
            return false;
        }

        um.attendeeAddEvent(attendeeID, getEventRoom(event).getRoomID(), event.getEventID());
        event.addAttendee(attendeeID);
        return true;
    }

    public boolean removeEventAttendee(UUID attendeeID, int roomNumber, int eventNumber, UserManager um) {
        Event event = getEvent(roomNumber, eventNumber);

        if (event.getAttendeeIDs().contains(attendeeID)) {
            um.attendeeRemoveEvent(attendeeID, getEventRoom(event).getRoomID(), event.getEventID());
            event.removeAttendee(attendeeID);
            return true;
        }
        return false;
    }

    public String stringEventsOfSpeaker(UserManager um, String speakerName) {
        StringBuilder s = new StringBuilder("Events by Speaker " + speakerName + ": \n");
        ArrayList<UUID> eventIDs = um.getSpeakerEventIDs(speakerName);
        ArrayList<Event> events = eventIDsToEvents(eventIDs);

        for (int eventNumber = 0; eventNumber < events.size(); eventNumber++) {
            Event event = events.get(eventNumber);
            s.append("(").append(eventNumber + 1).append(") ").append(event.toString()).append("\n");
        }
        return s.toString();
    }

    public String stringEventsOfRoom(int roomNumber) {
        StringBuilder s = new StringBuilder("Events in Room " + (roomNumber + 1) + ": \n");
        ArrayList<Event> events = getEventsFromRoom(roomNumber);
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            int eventNumber = i + 1;
            s.append("(").append(eventNumber).append(") ").append(event.toString()).append("\n");
        }
        return s.toString();
    }

    public String stringEventInfoAll() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < rooms.size(); i++) {
            s.append(stringEventsOfRoom(i));
        }
        return s.toString();
    }

    public String stringEventInfoAttending(UUID attendeeID) {
        StringBuilder s = new StringBuilder("All events: \n");
        for (Event event : getEvents()){
            if (getEventAttendeeIDs(event.getEventID()).contains(attendeeID)){
                s.append(event.toString());
            }
        } return s.toString();
    }

    public String stringEvent(UUID eventID) {
        return getEvent(eventID).toString();
    }
}
