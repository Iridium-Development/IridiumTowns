package com.iridium.iridiumtowns.configs;

import com.iridium.iridiumtowns.commands.ClaimCommand;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;

public class Commands extends com.iridium.iridiumteams.configs.Commands<Town, User> {
    public ClaimCommand claimCommand = new ClaimCommand();
}
