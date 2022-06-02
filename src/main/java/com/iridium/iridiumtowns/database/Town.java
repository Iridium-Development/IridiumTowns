package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.Team;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Town extends Team {
    public Town(String name) {
        setName(name);
        setDescription("Default Town Description :C");
    }

    public Town(int id) {
        setId(id);
    }
}
