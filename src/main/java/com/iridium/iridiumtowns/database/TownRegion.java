package com.iridium.iridiumtowns.database;

import com.iridium.iridiumtowns.IridiumTowns;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "town_regions")
public class TownRegion {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true)
    private int id;
    @Setter(AccessLevel.PRIVATE)
    @DatabaseField(columnName = "team_id", canBeNull = false)
    private Town town;

    @DatabaseField(columnName = "position_1", canBeNull = false)
    private Location position1;

    @DatabaseField(columnName = "position_2", canBeNull = false)
    private Location position2;

    public TownRegion(Town town, Location position1, Location position2) {
        this.town = town;
        this.position1 = position1;
        this.position2 = position2;
    }

    public boolean isInRegion(Location location) {
        if (location.getWorld() != position1.getWorld()) return false;
        return isInRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean isInRegion(int x, int y, int z) {
        boolean isWithinX = position1.getX() <= x && position2.getX() >= x;
        boolean isWithinY = position1.getY() <= y && position2.getY() >= y;
        boolean isWithinZ = position1.getZ() <= z && position2.getZ() >= z;
        return isWithinX && (isWithinY || IridiumTowns.getInstance().getConfiguration().ignoreYSpace) && isWithinZ;
    }

}
