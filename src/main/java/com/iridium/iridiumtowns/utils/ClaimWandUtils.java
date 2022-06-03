package com.iridium.iridiumtowns.utils;

import com.iridium.iridiumcore.dependencies.nbtapi.NBTCompound;
import com.iridium.iridiumcore.dependencies.nbtapi.NBTItem;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumtowns.IridiumTowns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClaimWandUtils {

    public static ItemStack getClaimWand() {
        NBTItem nbtItem = new NBTItem(ItemStackUtils.makeItem(IridiumTowns.getInstance().getConfiguration().claimWand, Arrays.asList(
                new Placeholder("position1", "N/A"),
                new Placeholder("position2", "N/A")
        )));
        NBTCompound nbtCompound = nbtItem.getOrCreateCompound("iridiumtowns");
        nbtCompound.setBoolean("claimWand", true);
        return nbtItem.getItem();
    }

    public static boolean isClaimWand(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("iridiumtowns");
        if (nbtCompound == null) return false;
        if (!nbtCompound.hasKey("claimWand")) return false;
        return nbtCompound.getBoolean("claimWand");
    }

    public static void setPosition1(ItemStack itemStack, Location location) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        NBTCompound nbtCompound = nbtItem.getOrCreateCompound("iridiumtowns").getOrCreateCompound("position1");

        nbtCompound.setString("world", location.getWorld().getName());
        nbtCompound.setInteger("x", location.getBlockX());
        nbtCompound.setInteger("y", location.getBlockY());
        nbtCompound.setInteger("z", location.getBlockZ());

        updateItemStack(itemStack);
    }

    public static void setPosition2(ItemStack itemStack, Location location) {
        NBTItem nbtItem = new NBTItem(itemStack, true);
        NBTCompound nbtCompound = nbtItem.getOrCreateCompound("iridiumtowns").getOrCreateCompound("position2");

        nbtCompound.setString("world", location.getWorld().getName());
        nbtCompound.setInteger("x", location.getBlockX());
        nbtCompound.setInteger("y", location.getBlockY());
        nbtCompound.setInteger("z", location.getBlockZ());

        updateItemStack(itemStack);
    }

    private static void updateItemStack(ItemStack itemStack) {
        List<Placeholder> placeholderList = Arrays.asList(
                new Placeholder("position1", getPosition1(itemStack).map(ClaimWandUtils::getLocationString).orElse("N/A")),
                new Placeholder("position2", getPosition2(itemStack).map(ClaimWandUtils::getLocationString).orElse("N/A"))
        );

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(StringUtils.color(StringUtils.processMultiplePlaceholders(IridiumTowns.getInstance().getConfiguration().claimWand.displayName, placeholderList)));
        itemMeta.setLore(StringUtils.color(StringUtils.processMultiplePlaceholders(IridiumTowns.getInstance().getConfiguration().claimWand.lore, placeholderList)));

        itemStack.setItemMeta(itemMeta);
    }

    public static Optional<Location> getPosition1(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return Optional.empty();
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("iridiumtowns");
        if (nbtCompound == null) return Optional.empty();
        NBTCompound position = nbtCompound.getCompound("position1");
        if (position == null) return Optional.empty();
        return Optional.of(new Location(Bukkit.getWorld(position.getString("world")), position.getInteger("x"), position.getInteger("y"), position.getInteger("z")));
    }

    public static Optional<Location> getPosition2(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return Optional.empty();
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("iridiumtowns");
        if (nbtCompound == null) return Optional.empty();
        NBTCompound position = nbtCompound.getCompound("position2");
        if (position == null) return Optional.empty();
        return Optional.of(new Location(Bukkit.getWorld(position.getString("world")), position.getInteger("x"), position.getInteger("y"), position.getInteger("z")));
    }

    private static String getLocationString(Location location) {
        return location.getWorld().getName() + " - " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

}
