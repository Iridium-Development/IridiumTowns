package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.TeamData;
import com.iridium.iridiumtowns.IridiumTowns;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "town_regions")
public class TownRegion extends TeamData {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true)
    private int id;

    @DatabaseField(columnName = "position_1", canBeNull = false)
    private Location position1;

    @DatabaseField(columnName = "position_2", canBeNull = false)
    private Location position2;

    public TownRegion(Town town, Location position1, Location position2) {
        super(town);
        this.position1 = new Location(
                position1.getWorld(),
                min(position1.getBlockX(), position2.getBlockX()),
                min(position1.getBlockY(), position2.getBlockY()),
                min(position1.getBlockZ(), position2.getBlockZ())
        );
        this.position2 = new Location(
                position1.getWorld(),
                max(position1.getBlockX(), position2.getBlockX()),
                max(position1.getBlockY(), position2.getBlockY()),
                max(position1.getBlockZ(), position2.getBlockZ())
        );
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

    public boolean isInRegion(Location position1, Location position2) {
        BoundingBox boundingBox = BoundingBox.of(position1, position2);
        return boundingBox.overlaps(BoundingBox.of(this.position1, this.position2));
    }

}
