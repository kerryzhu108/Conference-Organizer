package com.group0179.gui_bridge;

import com.group0179.controllers.LoginSystem;
import com.group0179.use_cases.MessageManager;
import com.group0179.use_cases.RoomManager;
import com.group0179.use_cases.UserManager;

public class InputFilter {
    private final UserManager um;
    private final RoomManager rm;
    private final MessageManager mm;

    private final int START_HOUR_EARLIEST = 9;
    private final int START_HOUR_LATEST = 16;

    public InputFilter(UserManager um, RoomManager rm, MessageManager mm) {
        this.um = um;
        this.rm = rm;
        this.mm = mm;
    }

    public boolean inputNewSpeakerUsername(String input) {
        if (!um.getUsernames().contains(input)) {
            um.createSpeakerAccount(input);
            return true;
        } return false;
    }
}
