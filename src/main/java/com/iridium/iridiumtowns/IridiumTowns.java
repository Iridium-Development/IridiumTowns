package com.iridium.iridiumtowns;

import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.PlaceholderBuilder;
import com.iridium.iridiumteams.configs.*;
import com.iridium.iridiumteams.managers.IridiumUserManager;
import com.iridium.iridiumteams.managers.TeamManager;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import com.iridium.iridiumtowns.managers.CommandManager;
import com.iridium.iridiumtowns.managers.TownManager;
import com.iridium.iridiumtowns.managers.UserManager;
import com.iridium.iridiumtowns.placeholders.UserPlaceholderBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class IridiumTowns extends IridiumTeams<Town, User> {

    private static IridiumTowns instance;

    private TownManager townManager;
    private UserManager userManager;
    private CommandManager commandManager;

    public IridiumTowns(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        instance = this;

        this.townManager = new TownManager();
        this.userManager = new UserManager();
        super.onEnable();
        this.commandManager = new CommandManager("iridiumtowns");
    }

    @Override
    public PlaceholderBuilder<Town> getTeamsPlaceholderBuilder() {
        return new PlaceholderBuilder<Town>() {
            @Override
            public List<Placeholder> getPlaceholders(Town town) {
                return Collections.emptyList();
            }

            @Override
            public List<Placeholder> getPlaceholders(Optional<Town> optional) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public PlaceholderBuilder<User> getUserPlaceholderBuilder() {
        return new UserPlaceholderBuilder();
    }

    @Override
    public TeamManager<Town, User> getTeamManager() {
        return townManager;
    }

    @Override
    public IridiumUserManager<Town, User> getUserManager() {
        return userManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration();
    }

    @Override
    public Messages getMessages() {
        return new Messages();
    }

    @Override
    public Permissions getPermissions() {
        return new Permissions();
    }

    @Override
    public Inventories getInventories() {
        return new Inventories();
    }

    @Override
    public Commands<Town, User> getCommands() {
        return new Commands<>();
    }

    public static IridiumTowns getInstance() {
        return instance;
    }
}
