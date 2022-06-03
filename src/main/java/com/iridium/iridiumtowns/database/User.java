package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumtowns.IridiumTowns;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class User extends IridiumUser<Town> {
    public User(UUID uuid, String name) {
        setUuid(uuid);
        setName(name);
        setJoinTime(LocalDateTime.now());
    }

    public Optional<Town> getTown(){
        return IridiumTowns.getInstance().getTeamManager().getTeamViaID(getTeamID());
    }
}
