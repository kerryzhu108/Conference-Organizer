import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages message entities.
 * @author Zachariah Vincze
 */
public class MessageManager {
    private Map<UUID, Message> messages;
    private UserManager userManager;
    private RoomManager roomManager;

    /**
     * Creates a new MessageManager.
     * @param userManager the users that this message system will be sending messages to.
     */
    public MessageManager(UserManager userManager, RoomManager roomManager) {
        this.userManager = userManager;
        this.roomManager = roomManager;
        this.messages = new HashMap<UUID, Message>();
    }

    /**
     * Sends messages from a single user to a list of recipients.
     * @param messageContent the string content of the message.
     * @param recipientIDs a list of users that are receiving the message.
     */
    public void sendMessages(String messageContent, List<UUID> recipientIDs) {
        UUID senderID = userManager.getCurrentUser().getUserID();
        Message message = new Message(messageContent, senderID, recipientIDs);
        messages.put(message.getMessageID(), message);
        for (UUID id : recipientIDs) {
            this.userManager.getUser(id).addMessage(senderID, message.getMessageID());
        }
    }

    /**
     * Sends a message from an organizer to all Attendees in the system.
     * @param messageContent the string content of the message.
     */
    public void sendMessageToAllAttendees(String messageContent) {
        List<UUID> attendees = userManager.getAttendees();
        this.sendMessages(messageContent, attendees);
    }
}
