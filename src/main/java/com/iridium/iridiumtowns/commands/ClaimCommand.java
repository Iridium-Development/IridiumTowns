package com.iridium.iridiumtowns.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.commands.Command;
import com.iridium.iridiumtowns.IridiumTowns;
import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.TownRegion;
import com.iridium.iridiumtowns.database.User;
import com.iridium.iridiumtowns.utils.ClaimWandUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClaimCommand extends Command<Town, User> {

    public ClaimCommand() {
        super(Collections.singletonList("claim"), "Claim land for your Town", "/town claim (confirm)", "");
    }

    @Override
    public void execute(User user, Town team, String[] args, IridiumTeams<Town, User> iridiumTeams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.getInventory().addItem(ClaimWandUtils.getClaimWand()).values().forEach(itemStack -> player.getLocation().getWorld().dropItem(player.getLocation(), itemStack));
            player.sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().givenClaimWand
                    .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
            ));
        } else if (args[0].equalsIgnoreCase("confirm")) {
            ItemStack claimWand = player.getItemInHand();
            if (!ClaimWandUtils.isClaimWand(claimWand)) {
                player.sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().notHoldingClaimWand
                        .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                ));
                return;
            }
            Optional<Location> position1 = ClaimWandUtils.getPosition1(claimWand);
            Optional<Location> position2 = ClaimWandUtils.getPosition2(claimWand);
            if (!position1.isPresent()) {
                player.sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().position1NotSet
                        .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                ));
                return;
            }
            if (!position2.isPresent()) {
                player.sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().position2NotSet
                        .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
                ));
                return;
            }

            IridiumTowns.getInstance().getDatabaseManager().getRegionsTableManager().addEntry(new TownRegion(team, position1.get(), position2.get()));
            player.sendMessage(StringUtils.color(IridiumTowns.getInstance().getMessages().claimSet
                    .replace("%prefix%", iridiumTeams.getConfiguration().prefix)
            ));
            player.setItemInHand(null);
        } else {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", iridiumTeams.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, IridiumTeams<Town, User> iridiumTeams) {
        if (args.length == 1) return Collections.singletonList("confirm");
        return super.onTabComplete(commandSender, args, iridiumTeams);
    }
}
