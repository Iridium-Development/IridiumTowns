package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumteams.database.*;
import com.iridium.iridiumteams.database.types.*;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.configs.SQL;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.TownRegion;
import com.iridium.iridiumtowns.managers.tablemanagers.ForeignTownTableManager;
import com.iridium.iridiumtowns.managers.tablemanagers.TableManager;
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
    private ForeignTownTableManager<TeamInvite, Integer> invitesTableManager;
    private ForeignTownTableManager<TeamPermission, Integer> permissionsTableManager;
    private ForeignTownTableManager<TeamBank, Integer> bankTableManager;
    private ForeignTownTableManager<TownRegion, Integer> regionsTableManager;
    private ForeignTownTableManager<TeamEnhancement, Integer> enhancementTableManager;
    private ForeignTownTableManager<TeamBlock, Integer> teamBlockTableManager;
    private ForeignTownTableManager<TeamSpawners, Integer> teamSpawnerTableManager;
    private ForeignTownTableManager<TeamWarp, Integer> teamWarpTableManager;
    private ForeignTownTableManager<TeamMission, Integer> teamMissionTableManager;
    private TableManager<TeamMissionData, Integer> teamMissionDataTableManager;
    private ForeignTownTableManager<TeamReward, Integer> teamRewardsTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumTowns.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocationType.getSingleton());
        DataPersisterManager.registerDataPersisters(InventoryType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocalDateTimeType.getSingleton());
        DataPersisterManager.registerDataPersisters(RewardType.getSingleton(IridiumTowns.getInstance()));

        this.connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        this.userTableManager = new UserTableManager(connectionSource);
        this.townTableManager = new TownTableManager(connectionSource);
        this.invitesTableManager = new ForeignTownTableManager<>(connectionSource, TeamInvite.class, Comparator.comparing(TeamInvite::getTeamID).thenComparing(TeamInvite::getUser));
        this.permissionsTableManager = new ForeignTownTableManager<>(connectionSource, TeamPermission.class, Comparator.comparing(TeamPermission::getTeamID).thenComparing(TeamPermission::getPermission));
        this.regionsTableManager = new ForeignTownTableManager<>(connectionSource, TownRegion.class, Comparator.comparing(TownRegion::getTeamID));
        this.bankTableManager = new ForeignTownTableManager<>(connectionSource, TeamBank.class, Comparator.comparing(TeamBank::getTeamID).thenComparing(TeamBank::getBankItem));
        this.enhancementTableManager = new ForeignTownTableManager<>(connectionSource, TeamEnhancement.class, Comparator.comparing(TeamEnhancement::getTeamID).thenComparing(TeamEnhancement::getEnhancementName));
        this.teamBlockTableManager = new ForeignTownTableManager<>(connectionSource, TeamBlock.class, Comparator.comparing(TeamBlock::getTeamID).thenComparing(TeamBlock::getXMaterial));
        this.teamSpawnerTableManager = new ForeignTownTableManager<>(connectionSource, TeamSpawners.class, Comparator.comparing(TeamSpawners::getTeamID).thenComparing(TeamSpawners::getEntityType));
        this.teamWarpTableManager = new ForeignTownTableManager<>(connectionSource, TeamWarp.class, Comparator.comparing(TeamWarp::getTeamID).thenComparing(TeamWarp::getName));
        this.teamMissionTableManager = new ForeignTownTableManager<>(connectionSource, TeamMission.class, Comparator.comparing(TeamMission::getTeamID).thenComparing(TeamMission::getMissionName));
        this.teamMissionDataTableManager = new TableManager<>(connectionSource, TeamMissionData.class, Comparator.comparing(TeamMissionData::getMissionID).thenComparing(TeamMissionData::getMissionIndex));
        this.teamRewardsTableManager = new ForeignTownTableManager<>(connectionSource, TeamReward.class, Comparator.comparing(TeamReward::getTeamID));
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
