package com.iridium.iridiumtowns;

import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.configs.BankItems;
import com.iridium.iridiumteams.configs.Missions;
import com.iridium.iridiumteams.managers.MissionManager;
import com.iridium.iridiumtowns.configs.*;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import com.iridium.iridiumtowns.listeners.PlayerInteractListener;
import com.iridium.iridiumtowns.listeners.PlayerMoveListener;
import com.iridium.iridiumtowns.managers.CommandManager;
import com.iridium.iridiumtowns.managers.DatabaseManager;
import com.iridium.iridiumtowns.managers.TownManager;
import com.iridium.iridiumtowns.managers.UserManager;
import com.iridium.iridiumtowns.placeholders.TeamChatPlaceholderBuilder;
import com.iridium.iridiumtowns.placeholders.TownPlaceholderBuilder;
import com.iridium.iridiumtowns.placeholders.UserPlaceholderBuilder;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.sql.SQLException;

@Getter
public class IridiumTowns extends IridiumTeams<Town, User> {

    private static IridiumTowns instance;

    private Configuration configuration;
    private Messages messages;
    private Permissions permissions;
    private Inventories inventories;
    private Commands commands;
    private BankItems bankItems;
    private Enhancements enhancements;
    private BlockValues blockValues;
    private Top top;
    private SQL sql;
    private Missions missions;

    private TownPlaceholderBuilder teamsPlaceholderBuilder;
    private UserPlaceholderBuilder userPlaceholderBuilder;
    private TeamChatPlaceholderBuilder teamChatPlaceholderBuilder;

    private TownManager teamManager;
    private UserManager userManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private MissionManager<Town, User> missionManager;

    private Economy economy;

    public IridiumTowns(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    public IridiumTowns() {
        instance = this;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.teamManager = new TownManager();
        this.userManager = new UserManager();
        this.commandManager = new CommandManager("iridiumtowns");
        this.databaseManager = new DatabaseManager();
        this.missionManager = new MissionManager<>(this);
        try {
            databaseManager.init();
        } catch (SQLException exception) {
            // We don't want the plugin to start if the connection fails
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.teamsPlaceholderBuilder = new TownPlaceholderBuilder();
        this.userPlaceholderBuilder = new UserPlaceholderBuilder();
        this.teamChatPlaceholderBuilder = new TeamChatPlaceholderBuilder();

        Bukkit.getScheduler().runTask(this, () -> this.economy = setupEconomy());
        super.onEnable();
    }

    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            getLogger().warning("You do not have an economy plugin installed (like Essentials)");
            return null;
        }
        return economyProvider.getProvider();
    }

    @Override
    public void registerListeners() {
        super.registerListeners();
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.commands = getPersist().load(Commands.class);
        this.sql = getPersist().load(SQL.class);
        this.inventories = getPersist().load(Inventories.class);
        this.permissions = getPersist().load(Permissions.class);
        this.bankItems = getPersist().load(BankItems.class);
        this.enhancements = getPersist().load(Enhancements.class);
        this.blockValues = getPersist().load(BlockValues.class);
        this.top = getPersist().load(Top.class);
        this.missions = getPersist().load(Missions.class);
        super.loadConfigs();
    }

    @Override
    public void saveConfigs() {
        super.saveConfigs();
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(commands);
        getPersist().save(sql);
        getPersist().save(inventories);
        getPersist().save(permissions);
        getPersist().save(bankItems);
        getPersist().save(enhancements);
        getPersist().save(blockValues);
        getPersist().save(top);
        getPersist().save(missions);
    }

    @Override
    public void saveData() {
        getDatabaseManager().getUserTableManager().save();
        getDatabaseManager().getTownTableManager().save();
        getDatabaseManager().getInvitesTableManager().save();
        getDatabaseManager().getPermissionsTableManager().save();
        getDatabaseManager().getRegionsTableManager().save();
        getDatabaseManager().getEnhancementTableManager().save();
        getDatabaseManager().getTeamBlockTableManager().save();
        getDatabaseManager().getTeamSpawnerTableManager().save();
        getDatabaseManager().getTeamMissionTableManager().save();
        getDatabaseManager().getTeamRewardsTableManager().save();
    }

    public static IridiumTowns getInstance() {
        return instance;
    }
}
