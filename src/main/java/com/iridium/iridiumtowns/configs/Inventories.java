package com.iridium.iridiumtowns.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumteams.configs.inventories.InventoryConfig;

import java.util.Collections;
import java.util.HashMap;

public class Inventories extends com.iridium.iridiumteams.configs.Inventories {
    @JsonIgnore
    private final Background background1 = new Background(new HashMap<>());

    public InventoryConfig townMenu = new InventoryConfig(45, "&7Town Menu", background1, ImmutableMap.<String, Item>builder()
            .put("t home", new Item(XMaterial.WHITE_BED, 13, 1, "&b&lTown Home", Collections.singletonList("&7Teleport to your town home")))
            .put("t boosters", new Item(XMaterial.EXPERIENCE_BOTTLE, 21, 1, "&b&lTown Boosters", Collections.singletonList("&7View your town boosters")))
            .put("t members", new Item(XMaterial.PLAYER_HEAD, 22, "Peaches_MLG", 1, "&b&lTown Members", Collections.singletonList("&7View your town members")))
            .put("t warps", new Item(XMaterial.END_PORTAL_FRAME, 23, 1, "&b&lTown Warps", Collections.singletonList("&7View your town warps")))
            .put("t upgrade", new Item(XMaterial.DIAMOND, 29, 1, "&b&lTown Upgrades", Collections.singletonList("&7View your town upgrades")))
            .put("t missions", new Item(XMaterial.IRON_SWORD, 30, 1, "&b&lTown Missions", Collections.singletonList("&7View your town missions")))
            .put("t bank", new Item(XMaterial.PLAYER_HEAD, 31, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM4MWM1MjlkNTJlMDNjZDc0YzNiZjM4YmI2YmEzZmRlMTMzN2FlOWJmNTAzMzJmYWE4ODllMGEyOGU4MDgxZiJ9fX0", 1, "&b&lTown Bank", Collections.singletonList("&7View your town bank")))
            .put("t permissions", new Item(XMaterial.WRITABLE_BOOK, 32, 1, "&b&lTown Permissions", Collections.singletonList("&7View your town permissions")))
            .put("t delete", new Item(XMaterial.BARRIER, 33, 1, "&b&lDelete Town", Collections.singletonList("&7Delete your town")))
            .build()
    );

    public Inventories() {
        super("Town", "&2");
    }
}
