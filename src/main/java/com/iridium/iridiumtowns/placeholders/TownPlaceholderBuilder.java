package com.iridium.iridiumtowns.placeholders;

import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumteams.PlaceholderBuilder;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TownPlaceholderBuilder implements PlaceholderBuilder<Town> {
    @Override
    public List<Placeholder> getPlaceholders(Town town) {
        List<User> users = IridiumTowns.getInstance().getTeamManager().getTeamMembers(town);
        List<String> onlineUsers = users.stream()
                .filter(u -> u.getPlayer() != null)
                .map(User::getName)
                .collect(Collectors.toList());
        List<String> offlineUsers = users.stream()
                .filter(u -> u.getPlayer() == null)
                .map(User::getName)
                .collect(Collectors.toList());
        return Arrays.asList(
                new Placeholder("town_name", town.getName()),
                new Placeholder("town_description", town.getDescription()),
                new Placeholder("town_members_online", String.join(", ", onlineUsers)),
                new Placeholder("town_members_online_count", String.valueOf(onlineUsers.size())),
                new Placeholder("town_members_offline", String.join(", ", offlineUsers)),
                new Placeholder("town_members_offline_count", String.valueOf(offlineUsers.size())),
                new Placeholder("town_members_count", String.valueOf(users.size()))
        );
    }

    public List<Placeholder> getDefaultPlaceholders() {
        return Arrays.asList(
                new Placeholder("town_name", "N/A"),
                new Placeholder("town_description", "N/A"),
                new Placeholder("town_members_online", "N/A"),
                new Placeholder("town_members_online_count", "N/A"),
                new Placeholder("town_members_offline", "N/A"),
                new Placeholder("town_members_offline_count", "N/A"),
                new Placeholder("town_members_count", "N/A")
        );
    }

    @Override
    public List<Placeholder> getPlaceholders(Optional<Town> optional) {
        return optional.isPresent() ? getPlaceholders(optional.get()) : getDefaultPlaceholders();
    }
}
