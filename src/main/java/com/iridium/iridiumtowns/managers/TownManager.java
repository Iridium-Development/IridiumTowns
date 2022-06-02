package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumteams.CreateCancelledException;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.Team;
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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TownManager extends TeamManager<Town, User> {

    public static List<TeamPermission> teamPermissions;
    public static List<TeamInvite> teamInvites;
    public static List<TownRegion> townRegions;

    public TownManager() {
        teamPermissions = new ArrayList<>();
        teamInvites = new ArrayList<>();
        townRegions = new ArrayList<>();
    }

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
        return townRegions.stream()
                .filter(townRegion -> townRegion.isInRegion(location))
                .map(TownRegion::getTown)
                .findFirst();
    }

    @Override
    public Optional<Town> getTeamViaNameOrPlayer(String name) {
        if (name == null || name.equals("")) return Optional.empty();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(name);
        Optional<Town> team = getTeamViaID(IridiumTowns.getInstance().getUserManager().getUser(targetPlayer).getTeamID());
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

            World world = Bukkit.getServer().getWorld("world");
            townRegions.add(new TownRegion(town, new Location(world, -10, 0, -10), new Location(world, 10, 0, 10)));

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
    public boolean getTeamPermission(Team team, int rank, String permission) {
        if (rank == Rank.OWNER.getId()) return true;
        return teamPermissions.stream()
                .filter(teamPermission -> teamPermission.getTeamID() == team.getId() && teamPermission.getPermission().equals(permission))
                .findFirst()
                .map(TeamPermission::isAllowed)
                .orElse(IridiumTowns.getInstance().getPermissionList().get(permission).getDefaultRank() <= rank);
    }

    @Override
    public void setTeamPermission(Team team, int rank, String permission, boolean allowed) {
        Optional<TeamPermission> teamPermission = teamPermissions.stream()
                .filter(perm -> perm.getTeamID() == team.getId() && perm.getPermission().equals(permission))
                .findFirst();
        if (teamPermission.isPresent()) {
            teamPermission.get().setAllowed(allowed);
        } else {
            teamPermissions.add(new TeamPermission(team, permission, rank, allowed));
        }
    }

    @Override
    public Optional<TeamInvite> getTeamInvite(Town team, User user) {
        return teamInvites.stream()
                .filter(teamInvite -> teamInvite.getTeamID() == team.getId())
                .filter(teamInvite -> teamInvite.getUser() == user.getUuid())
                .findFirst();
    }

    @Override
    public List<TeamInvite> getTeamInvites(Town team) {
        return teamInvites.stream()
                .filter(teamInvite -> teamInvite.getTeamID() == team.getId())
                .collect(Collectors.toList());
    }

    @Override
    public void createTeamInvite(Town team, User user, User invitee) {
        teamInvites.add(new TeamInvite(team, user.getUuid(), invitee.getUuid()));
    }

    @Override
    public void deleteTeamInvite(TeamInvite teamInvite) {
        teamInvites.remove(teamInvite);
    }
}
