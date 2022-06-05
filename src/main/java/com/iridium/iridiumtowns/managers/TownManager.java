package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.TeamBank;
import com.iridium.iridiumteams.database.TeamInvite;
import com.iridium.iridiumteams.database.TeamPermission;
import com.iridium.iridiumteams.managers.TeamManager;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.TownRegion;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TownManager extends TeamManager<Town, User> {

    @Override
    public Optional<Town> getTeamViaID(int id) {
        return IridiumTowns.getInstance().getDatabaseManager().getTownTableManager().getTown(id);
    }

    @Override
    public Optional<Town> getTeamViaName(String name) {
        return IridiumTowns.getInstance().getDatabaseManager().getTownTableManager().getTown(name);
    }

    @Override
    public Optional<Town> getTeamViaLocation(Location location) {
        return IridiumTowns.getInstance().getDatabaseManager().getRegionsTableManager().getEntries().stream()
                .filter(townRegion -> townRegion.isInRegion(location))
                .map(TownRegion::getTeamID)
                .map(this::getTeamViaID)
                .findFirst()
                .orElse(Optional.empty());
    }

    @Override
    public Optional<Town> getTeamViaNameOrPlayer(String name) {
        if (name == null || name.equals("")) return Optional.empty();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(name);
        Optional<Town> team = IridiumTowns.getInstance().getUserManager().getUser(targetPlayer).getTown();
        if (!team.isPresent()) {
            return getTeamViaName(name);
        }
        return team;
    }

    @Override
    public List<User> getTeamMembers(Town team) {
        return IridiumTowns.getInstance().getDatabaseManager().getUserTableManager().getEntries().stream()
                .filter(user -> user.getTeamID() == team.getId())
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<Town> createTeam(@NotNull Player owner, @NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            User user = IridiumTowns.getInstance().getUserManager().getUser(owner);
            Town town = new Town(name);

            IridiumTowns.getInstance().getDatabaseManager().registerTown(town).join();

            user.setTeam(town);
            user.setUserRank(Rank.OWNER.getId());

            return town;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Override
    public void deleteTeam(Town town, User user) {
        IridiumTowns.getInstance().getDatabaseManager().getTownTableManager().delete(town);
    }

    @Override
    public boolean getTeamPermission(Town town, int rank, String permission) {
        if (rank == Rank.OWNER.getId()) return true;
        return IridiumTowns.getInstance().getDatabaseManager().getPermissionsTableManager().getEntry(new TeamPermission(town, permission, rank, true))
                .map(TeamPermission::isAllowed)
                .orElse(IridiumTowns.getInstance().getPermissionList().get(permission).getDefaultRank() <= rank);
    }

    @Override
    public synchronized void setTeamPermission(Town town, int rank, String permission, boolean allowed) {
        TeamPermission townPermission = new TeamPermission(town, permission, rank, true);
        Optional<TeamPermission> teamPermission = IridiumTowns.getInstance().getDatabaseManager().getPermissionsTableManager().getEntry(townPermission);
        if (teamPermission.isPresent()) {
            teamPermission.get().setAllowed(allowed);
        } else {
            IridiumTowns.getInstance().getDatabaseManager().getPermissionsTableManager().addEntry(townPermission);
        }
    }

    @Override
    public Optional<TeamInvite> getTeamInvite(Town team, User user) {
        return IridiumTowns.getInstance().getDatabaseManager().getInvitesTableManager().getEntry(new TeamInvite(team, user.getUuid(), user.getUuid()));
    }

    @Override
    public List<TeamInvite> getTeamInvites(Town team) {
        return IridiumTowns.getInstance().getDatabaseManager().getInvitesTableManager().getEntries(team);
    }

    @Override
    public void createTeamInvite(Town team, User user, User invitee) {
        IridiumTowns.getInstance().getDatabaseManager().getInvitesTableManager().addEntry(new TeamInvite(team, user.getUuid(), invitee.getUuid()));
    }

    @Override
    public void deleteTeamInvite(TeamInvite teamInvite) {
        IridiumTowns.getInstance().getDatabaseManager().getInvitesTableManager().delete(teamInvite);
    }

    @Override
    public synchronized TeamBank getTeamBank(Town town, String bankItem) {
        Optional<TeamBank> teamBank = IridiumTowns.getInstance().getDatabaseManager().getBankTableManager().getEntry(new TeamBank(town, bankItem, 0));
        if (teamBank.isPresent()) {
            return teamBank.get();
        } else {
            TeamBank bank = new TeamBank(town, bankItem, 0);
            IridiumTowns.getInstance().getDatabaseManager().getBankTableManager().addEntry(bank);
            return bank;
        }
    }
}
