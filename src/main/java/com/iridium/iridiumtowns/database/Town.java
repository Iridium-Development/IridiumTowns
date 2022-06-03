package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.Team;
import com.iridium.iridiumtowns.IridiumTowns;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Town extends Team {
    public Town(String name) {
        setName(name);
        setDescription(IridiumTowns.getInstance().getConfiguration().defaultDescription);
    }

    public Town(int id) {
        setId(id);
    }
}
