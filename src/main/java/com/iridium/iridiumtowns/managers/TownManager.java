package com.iridium.iridiumtowns.managers;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.database.*;
import com.iridium.iridiumteams.managers.TeamManager;
import com.iridium.iridiumteams.missions.Mission;
import com.iridium.iridiumteams.missions.MissionType;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.TownRegion;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TownManager extends TeamManager<Town, User> {

    public TownManager() {
        super(IridiumTowns.getInstance());
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
    public List<Town> getTeams() {
        return IridiumTowns.getInstance().getDatabaseManager().getTownTableManager().getEntries();
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

    @Override
    public synchronized TeamSpawners getTeamSpawners(Town town, EntityType entityType) {
        Optional<TeamSpawners> teamSpawner = IridiumTowns.getInstance().getDatabaseManager().getTeamSpawnerTableManager().getEntry(new TeamSpawners(town, entityType, 0));
        if (teamSpawner.isPresent()) {
            return teamSpawner.get();
        } else {
            TeamSpawners spawner = new TeamSpawners(town, entityType, 0);
            IridiumTowns.getInstance().getDatabaseManager().getTeamSpawnerTableManager().addEntry(spawner);
            return spawner;
        }
    }

    @Override
    public synchronized TeamBlock getTeamBlock(Town town, XMaterial xMaterial) {
        Optional<TeamBlock> teamBlock = IridiumTowns.getInstance().getDatabaseManager().getTeamBlockTableManager().getEntry(new TeamBlock(town, xMaterial, 0));
        if (teamBlock.isPresent()) {
            return teamBlock.get();
        } else {
            TeamBlock block = new TeamBlock(town, xMaterial, 0);
            IridiumTowns.getInstance().getDatabaseManager().getTeamBlockTableManager().addEntry(block);
            return block;
        }
    }

    @Override
    public TeamEnhancement getTeamEnhancement(Town town, String enhancementName) {
        Optional<TeamEnhancement> teamEnhancement = IridiumTowns.getInstance().getDatabaseManager().getEnhancementTableManager().getEntry(new TeamEnhancement(town, enhancementName, 0));
        if (teamEnhancement.isPresent()) {
            return teamEnhancement.get();
        } else {
            TeamEnhancement enhancement = new TeamEnhancement(town, enhancementName, 0);
            IridiumTowns.getInstance().getDatabaseManager().getEnhancementTableManager().addEntry(enhancement);
            return enhancement;
        }
    }

    public CompletableFuture<List<CreatureSpawner>> getSpawners(Chunk chunk, TownRegion townRegion) {
        CompletableFuture<List<CreatureSpawner>> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(IridiumTowns.getInstance(), () -> {
            List<CreatureSpawner> creatureSpawners = new ArrayList<>();
            for (BlockState blockState : chunk.getTileEntities()) {
                if (!townRegion.isInRegion(blockState.getLocation())) continue;
                if (!(blockState instanceof CreatureSpawner)) continue;
                creatureSpawners.add((CreatureSpawner) blockState);
            }
            completableFuture.complete(creatureSpawners);
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Void> recalculateTeam(Town town) {
        Map<XMaterial, Integer> teamBlocks = new HashMap<>();
        Map<EntityType, Integer> teamSpawners = new HashMap<>();
        return CompletableFuture.runAsync(() -> {
            List<TownRegion> townRegions = IridiumTowns.getInstance().getDatabaseManager().getRegionsTableManager().getEntries(town);
            for (TownRegion townRegion : townRegions) {
                List<Chunk> chunks = getTownChunks(townRegion).join();
                for (Chunk chunk : chunks) {
                    ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(true, false, false);
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            final int maxy = chunkSnapshot.getHighestBlockYAt(x, z);
                            for (int y = 0; y <= maxy; y++) {
                                if (townRegion.isInRegion(x + (chunkSnapshot.getX() * 16), y, z + (chunkSnapshot.getZ() * 16))) {
                                    XMaterial material = XMaterial.matchXMaterial(chunkSnapshot.getBlockType(x, y, z));
                                    teamBlocks.put(material, teamBlocks.getOrDefault(material, 0) + 1);
                                }
                            }
                        }
                    }
                    getSpawners(chunk, townRegion).join().forEach(creatureSpawner ->
                            teamSpawners.put(creatureSpawner.getSpawnedType(), teamSpawners.getOrDefault(creatureSpawner.getSpawnedType(), 0) + 1)
                    );
                }
            }
        }).thenRun(() -> Bukkit.getScheduler().runTask(IridiumTowns.getInstance(), () -> {
            List<TeamBlock> blocks = IridiumTowns.getInstance().getDatabaseManager().getTeamBlockTableManager().getEntries(town);
            List<TeamSpawners> spawners = IridiumTowns.getInstance().getDatabaseManager().getTeamSpawnerTableManager().getEntries(town);
            for (TeamBlock teamBlock : blocks) {
                teamBlock.setAmount(teamBlocks.getOrDefault(teamBlock.getXMaterial(), 0));
            }
            for (TeamSpawners teamSpawner : spawners) {
                teamSpawner.setAmount(teamSpawners.getOrDefault(teamSpawner.getEntityType(), 0));
            }
        }));
    }

    @Override
    public void createWarp(Town town, UUID creator, Location location, String name, String password) {
        IridiumTowns.getInstance().getDatabaseManager().getTeamWarpTableManager().addEntry(new TeamWarp(town, creator, location, name, password));
    }

    @Override
    public void deleteWarp(TeamWarp teamWarp) {
        IridiumTowns.getInstance().getDatabaseManager().getTeamWarpTableManager().delete(teamWarp);
    }

    @Override
    public List<TeamWarp> getTeamWarps(Town town) {
        return IridiumTowns.getInstance().getDatabaseManager().getTeamWarpTableManager().getEntries(town);
    }

    @Override
    public Optional<TeamWarp> getTeamWarp(Town town, String name) {
        return IridiumTowns.getInstance().getDatabaseManager().getTeamWarpTableManager().getEntry(new TeamWarp(town, UUID.randomUUID(), null, name));
    }

    @Override
    public List<TeamMission> getTeamMissions(Town town) {
        return IridiumTowns.getInstance().getDatabaseManager().getTeamMissionTableManager().getEntries(town);
    }

    @Override
    public TeamMission getTeamMission(Town town, String missionName) {
        Mission mission = IridiumTowns.getInstance().getMissions().missions.get(missionName);
        LocalDateTime localDateTime = IridiumTowns.getInstance().getMissionManager().getExpirationTime(mission == null ? MissionType.ONCE : mission.getMissionType(), LocalDateTime.now());

        TeamMission newTeamMission = new TeamMission(town, missionName, localDateTime);
        Optional<TeamMission> teamMission = IridiumTowns.getInstance().getDatabaseManager().getTeamMissionTableManager().getEntry(newTeamMission);
        if (teamMission.isPresent()) {
            return teamMission.get();
        } else {
            IridiumTowns.getInstance().getDatabaseManager().getTeamMissionTableManager().addEntry(newTeamMission);
            return newTeamMission;
        }
    }

    @Override
    public TeamMissionData getTeamMissionData(TeamMission teamMission, int missionIndex) {
        Optional<TeamMissionData> teamMissionData = IridiumTowns.getInstance().getDatabaseManager().getTeamMissionDataTableManager().getEntry(new TeamMissionData(teamMission, missionIndex));
        if (teamMissionData.isPresent()) {
            return teamMissionData.get();
        } else {
            TeamMissionData missionData = new TeamMissionData(teamMission, missionIndex);
            IridiumTowns.getInstance().getDatabaseManager().getTeamMissionDataTableManager().addEntry(missionData);
            return missionData;
        }
    }

    @Override
    public void deleteTeamMission(TeamMission teamMission) {
        IridiumTowns.getInstance().getDatabaseManager().getTeamMissionTableManager().delete(teamMission);
    }

    @Override
    public void deleteTeamMissionData(TeamMission teamMission) {
        List<TeamMissionData> teamMissionDataList = IridiumTowns.getInstance().getDatabaseManager().getTeamMissionDataTableManager().getEntries().stream()
                .filter(teamMissionData -> teamMissionData.getMissionID() == teamMission.getId())
                .collect(Collectors.toList());
        IridiumTowns.getInstance().getDatabaseManager().getTeamMissionDataTableManager().delete(teamMissionDataList);
    }

    @Override
    public List<TeamReward> getTeamRewards(Town town) {
        return IridiumTowns.getInstance().getDatabaseManager().getTeamRewardsTableManager().getEntries(town);
    }

    @Override
    public void addTeamReward(TeamReward teamReward) {
        IridiumTowns.getInstance().getDatabaseManager().getTeamRewardsTableManager().addEntry(teamReward);
    }

    @Override
    public void deleteTeamReward(TeamReward teamReward) {
        IridiumTowns.getInstance().getDatabaseManager().getTeamRewardsTableManager().delete(teamReward);
    }

    public CompletableFuture<List<Chunk>> getTownChunks(TownRegion townRegion) {
        return CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture<Chunk>> chunks = new ArrayList<>();

            Location pos1 = townRegion.getPosition1();
            Location pos2 = townRegion.getPosition2();

            int minX = pos1.getBlockX() >> 4;
            int minZ = pos1.getBlockZ() >> 4;
            int maxX = pos2.getBlockX() >> 4;
            int maxZ = pos2.getBlockZ() >> 4;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    chunks.add(IridiumTowns.getInstance().getMultiVersion().getChunkAt(pos1.getWorld(), x, z));
                }
            }
            return chunks.stream().map(CompletableFuture::join).collect(Collectors.toList());
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return Collections.emptyList();
        });
    }
}
