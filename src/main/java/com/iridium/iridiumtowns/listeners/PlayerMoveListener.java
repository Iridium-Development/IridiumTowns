package com.iridium.iridiumtowns.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) return;
        if (event.getFrom().getBlock().getLocation().equals(event.getTo().getBlock().getLocation())) return;

        Optional<Town> fromTown = IridiumTowns.getInstance().getTeamManager().getTeamViaLocation(event.getFrom());
        Optional<Town> toTown = IridiumTowns.getInstance().getTeamManager().getTeamViaLocation(event.getTo());

        if (!toTown.isPresent()) return;
        if (toTown.get().getId() != (fromTown.map(Town::getId).orElse(0))) {
            IridiumTowns.getInstance().getNms().sendTitle(player, StringUtils.color("&2") + toTown.get().getName(), StringUtils.color(ChatColor.GRAY + toTown.get().getDescription()), 20, 40, 20);
        }
    }

}
