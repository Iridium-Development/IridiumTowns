package com.iridium.iridiumtowns.placeholders;

import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumteams.PlaceholderBuilder;
import com.iridium.iridiumteams.UserRank;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.User;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserPlaceholderBuilder implements PlaceholderBuilder<User> {
    @Override
    public List<Placeholder> getPlaceholders(User user) {
        return Arrays.asList(
                new Placeholder("player_rank", IridiumTowns.getInstance().getUserRanks().getOrDefault(user.getUserRank(), new UserRank("N/A", null)).name),
                new Placeholder("player_name", user.getName()),
                new Placeholder("player_join", user.getJoinTime().format(DateTimeFormatter.ofPattern(IridiumTowns.getInstance().getConfiguration().dateTimeFormat)))
        );
    }

    public List<Placeholder> getDefaultPlaceholders() {
        return Arrays.asList(
                new Placeholder("player_rank", "N/A"),
                new Placeholder("player_name", "N/A"),
                new Placeholder("player_join", "N/A")
        );
    }

    @Override
    public List<Placeholder> getPlaceholders(Optional<User> optional) {
        return optional.isPresent() ? getPlaceholders(optional.get()) : getDefaultPlaceholders();
    }
}
