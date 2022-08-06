package com.iridium.iridiumtowns.placeholders;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumteams.PlaceholderBuilder;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
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

        List<Placeholder> placeholderList = new ArrayList<>(Arrays.asList(
                new Placeholder("town_name", town.getName()),
                new Placeholder("town_owner", IridiumTowns.getInstance().getTeamManager().getTeamMembers(town).stream()
                        .filter(user -> user.getUserRank() == Rank.OWNER.getId())
                        .findFirst()
                        .map(User::getName)
                        .orElse("N/A")),
                new Placeholder("town_description", town.getDescription()),
                new Placeholder("town_value", String.valueOf(IridiumTowns.getInstance().getTeamManager().getTeamValue(town))),
                new Placeholder("town_level", String.valueOf(town.getLevel())),
                new Placeholder("town_experience", String.valueOf(town.getExperience())),
                new Placeholder("town_value_rank", String.valueOf(IridiumTowns.getInstance().getTop().valueTeamSort.getRank(town, IridiumTowns.getInstance()))),
                new Placeholder("town_experience_rank", String.valueOf(IridiumTowns.getInstance().getTop().experienceTeamSort.getRank(town, IridiumTowns.getInstance()))),
                new Placeholder("town_members_online", String.join(", ", onlineUsers)),
                new Placeholder("town_members_online_count", String.valueOf(onlineUsers.size())),
                new Placeholder("town_members_offline", String.join(", ", offlineUsers)),
                new Placeholder("town_members_offline_count", String.valueOf(offlineUsers.size())),
                new Placeholder("town_members_count", String.valueOf(users.size()))
        ));
        for (XMaterial xMaterial : XMaterial.values()) {
            placeholderList.add(new Placeholder(xMaterial.name().toUpperCase() + "_AMOUNT", String.valueOf(IridiumTowns.getInstance().getTeamManager().getTeamBlock(town, xMaterial).getAmount())));
        }
        for (EntityType entityType : EntityType.values()) {
            placeholderList.add(new Placeholder(entityType.name().toUpperCase() + "_AMOUNT", String.valueOf(IridiumTowns.getInstance().getTeamManager().getTeamSpawners(town, entityType).getAmount())));
        }
        return placeholderList;
    }

    public List<Placeholder> getDefaultPlaceholders() {
        List<Placeholder> placeholderList = new ArrayList<>(Arrays.asList(
                new Placeholder("town_name", "N/A"),
                new Placeholder("town_owner", "N/A"),
                new Placeholder("town_description", "N/A"),
                new Placeholder("town_value", "N/A"),
                new Placeholder("town_level", "N/A"),
                new Placeholder("town_experience", "N/A"),
                new Placeholder("town_value_rank", "N/A"),
                new Placeholder("town_experience_rank", "N/A"),
                new Placeholder("town_members_online", "N/A"),
                new Placeholder("town_members_online_count", "N/A"),
                new Placeholder("town_members_offline", "N/A"),
                new Placeholder("town_members_offline_count", "N/A"),
                new Placeholder("town_members_count", "N/A")
        ));
        for (XMaterial xMaterial : XMaterial.values()) {
            placeholderList.add(new Placeholder(xMaterial.name().toUpperCase() + "_AMOUNT", "N/A"));
        }
        for (EntityType entityType : EntityType.values()) {
            placeholderList.add(new Placeholder(entityType.name().toUpperCase() + "_AMOUNT", "N/A"));
        }
        return placeholderList;
    }

    @Override
    public List<Placeholder> getPlaceholders(Optional<Town> optional) {
        return optional.isPresent() ? getPlaceholders(optional.get()) : getDefaultPlaceholders();
    }
}
