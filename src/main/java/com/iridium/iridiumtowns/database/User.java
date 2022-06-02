package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.IridiumUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class User extends IridiumUser<Town> {
    private Location position1;
    private Location position2;

    public User(UUID uuid, String name) {
        setUuid(uuid);
        setName(name);
    }
}
