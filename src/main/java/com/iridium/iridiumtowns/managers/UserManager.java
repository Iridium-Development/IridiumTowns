
package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumteams.managers.IridiumUserManager;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserManager implements IridiumUserManager<Town, User> {

    @Override
    public @NotNull User getUser(@NotNull OfflinePlayer offlinePlayer) {
        Optional<User> userOptional = getUserByUUID(offlinePlayer.getUniqueId());
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            Optional<String> name = Optional.ofNullable(offlinePlayer.getName());
            User user = new User(offlinePlayer.getUniqueId(), name.orElse(""));
            IridiumTowns.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);
            return user;
        }
    }

    public Optional<User> getUserByUUID(@NotNull UUID uuid) {
        return IridiumTowns.getInstance().getDatabaseManager().getUserTableManager().getUser(uuid);
    }

    @Override
    public List<User> getUsers() {
        return IridiumTowns.getInstance().getDatabaseManager().getUserTableManager().getEntries();
    }
}
