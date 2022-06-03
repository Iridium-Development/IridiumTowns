package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumteams.database.TeamInvite;
import com.iridium.iridiumteams.database.TeamPermission;
import com.iridium.iridiumteams.database.types.LocalDateTimeType;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.configs.SQL;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.TownRegion;
import com.iridium.iridiumtowns.database.types.InventoryType;
import com.iridium.iridiumtowns.database.types.LocationType;
import com.iridium.iridiumtowns.database.types.XMaterialType;
import com.iridium.iridiumtowns.managers.tablemanagers.ForeignTownTableManager;
import com.iridium.iridiumtowns.managers.tablemanagers.TownTableManager;
import com.iridium.iridiumtowns.managers.tablemanagers.UserTableManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

@Getter
public class DatabaseManager {

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private UserTableManager userTableManager;
    private TownTableManager townTableManager;
    private ForeignTownTableManager<TeamInvite<Town>, Integer> invitesTableManager;
    private ForeignTownTableManager<TeamPermission<Town>, Integer> permissionsTableManager;
    private ForeignTownTableManager<TownRegion, Integer> regionsTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumTowns.getInstance().getSQL();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocationType.getSingleton());
        DataPersisterManager.registerDataPersisters(InventoryType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocalDateTimeType.getSingleton());

        this.connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        this.userTableManager = new UserTableManager(connectionSource);
        this.townTableManager = new TownTableManager(connectionSource);
        this.invitesTableManager = new ForeignTownTableManager<>(connectionSource, (Class<TeamInvite<Town>>) (Class<?>) TeamInvite.class, Comparator.comparing((TeamInvite<Town> t) -> t.getTeamID()).thenComparing(TeamInvite::getUser));
        this.permissionsTableManager = new ForeignTownTableManager<>(connectionSource, (Class<TeamPermission<Town>>) (Class<?>) TeamPermission.class, Comparator.comparing((TeamPermission<Town> t) -> t.getTeamID()).thenComparing(TeamPermission::getPermission));
        this.regionsTableManager = new ForeignTownTableManager<>(connectionSource, TownRegion.class, Comparator.comparing(TownRegion::getTeamID));
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    private @NotNull String getDatabaseURL(SQL sqlConfig) {
        switch (sqlConfig.driver) {
            case MYSQL:
                return "jdbc:" + sqlConfig.driver.name().toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL;
            case SQLITE:
                return "jdbc:sqlite:" + new File(IridiumTowns.getInstance().getDataFolder(), sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driver.name());
        }
    }

    public CompletableFuture<Void> registerTown(Town town) {
        return CompletableFuture.runAsync(() -> {
            townTableManager.addEntry(town);
            // Saving the object will also assign the Faction's ID
            townTableManager.save();
            // Since the FactionID was null before we need to resort
            townTableManager.sort();
        });
    }

}
