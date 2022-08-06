package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandManager extends com.iridium.iridiumteams.managers.CommandManager<Town, User> {
    public CommandManager(String command) {
        super(IridiumTowns.getInstance(), "&2", command);
        registerCommand(IridiumTowns.getInstance().getCommands().claimCommand);
    }

    @Override
    public void noArgsDefault(@NotNull CommandSender commandSender) {
        Bukkit.getServer().dispatchCommand(commandSender, "town help");
    }
}
