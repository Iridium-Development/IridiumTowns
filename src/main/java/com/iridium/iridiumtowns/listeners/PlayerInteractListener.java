package com.iridium.iridiumtowns.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.utils.ClaimWandUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if (!ClaimWandUtils.isClaimWand(itemStack)) return;
        if (event.getAction() == Action.PHYSICAL) return;
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            ClaimWandUtils.setPosition1(itemStack, event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().position1Set
                    .replace("%prefix%", IridiumTowns.getInstance().getConfiguration().prefix)
            ));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            ClaimWandUtils.setPosition2(itemStack, event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().position2Set
                    .replace("%prefix%", IridiumTowns.getInstance().getConfiguration().prefix)
            ));
        }
    }

}
