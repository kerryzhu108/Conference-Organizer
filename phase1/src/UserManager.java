import java.util.*;

/**
 * Get list of all users, attendees, speakers, or organizers in existence
 *
 * Get a list of all usernames in existence
 *
 * @author Zihan Wang, Justin Chan, Kaiyi Liu
 * Last modified: Justin Chan
 */

public class UserManager {
    private final ArrayList<Attendee> attendees = new ArrayList<>();
    private final ArrayList<Organizer> organizers = new ArrayList<>();
    private final ArrayList<Speaker> speakers = new ArrayList<>();
    private User currentUser;

    /**
     * Last modified: Justin Chan
     */
    public UserManager() { }

    /**
     * Created: Justin Chan
     * @return an ArrayList of all users in existence
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.addAll(attendees);
        users.addAll(organizers);
        users.addAll(speakers);
        return users;
    }

    public ArrayList<String> getUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        for (User user : getUsers()) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    /**
     * Created: Justin Chan
     * @return a dictionary mapping UserID to User
     */
    public HashMap<UUID, User> getUserIDToUser() {
        HashMap<UUID, User> userIDToUser =  new HashMap<>();
        for (User user : getUsers()) {
            userIDToUser.put(user.getUserID(), user);
        }
        return userIDToUser;
    }

    /**
     * Returns a User object based on their UUID.
     * @param userID the UUID of the user you wish to return.
     * @return the User that the provided UUID belongs to or null if that user is not found.
     * Last modified: Justin Chan
     */
    public User getUser(UUID userID) {
        return getUserIDToUser().get(userID);
    }

    /**
     * @return list of attendees in existence
     * Last modified: Justin Chan
     */
    public ArrayList<Attendee> getAttendees() {
        return attendees;
    }

    /**
     * Created: Justin Chan
     * @return list of speakers in existence
     */
    public ArrayList<Speaker> getSpeakers() {
        return speakers;
    }

    /**
     * Created: Justin Chan
     * @return list of organizers in existence
     */
    public ArrayList<Organizer> getOrganizers() {
        return organizers;
    }

    /**
     * @return currently logged in User.
     */
    public User getCurrentUser() { return currentUser; }

    /**
     * Sets the currently logged in user.
     * @param currentUser the logged in user.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @param username unique username requested
     * @return true if the account was created and false if the account could not be created
     */
    public boolean createAttendeeAccount(String username){
        if (getUsernames().contains(username)) {
            return false;
        }
        Attendee attendee = new Attendee(username);
        attendees.add(attendee);
        return true;
    }

    /**
     * @param username unique username requested
     * @return true if the account was created and false if the account could not be created
     */
    public boolean createOrganizerAccount(String username){
        if (getUsernames().contains(username)) {
            return false;
        }
        Organizer organizer = new Organizer(username);
        organizers.add(organizer);
        return true;
    }

    /**
     * @param username unique username requested
     * @return true if the account was created and false if the account could not be created
     */
    public boolean createSpeakerAccount(String username){
        if (getUsernames().contains(username)) {
            return false;
        }
        Speaker speaker = new Speaker(username);
        speakers.add(speaker);
        return true;
    }
}
