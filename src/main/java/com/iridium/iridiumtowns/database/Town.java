package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.Team;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.managers.TownManager;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;

@NoArgsConstructor
public class Town extends Team {
    public Town(String name) {
        setName(name);
        setDescription(IridiumTowns.getInstance().getConfiguration().defaultDescription);
    }

    public Town(int id) {
        setId(id);
    }

    @Override
    public int getLevel() {
        return IridiumTowns.getInstance().getTeamManager().getTeamLevel(getExperience());
    }

    @Override
    public double getValue() {
        return IridiumTowns.getInstance().getTeamManager().getTeamValue(this);
    }
}
