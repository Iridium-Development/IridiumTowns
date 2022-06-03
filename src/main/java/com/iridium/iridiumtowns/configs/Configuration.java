package com.iridium.iridiumtowns.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;

import java.util.Arrays;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";
    public boolean ignoreYSpace = true;
    public Item claimWand = new Item(XMaterial.GOLDEN_AXE, 1, "&9&lClaim Wand", Arrays.asList(
            "&7Position 1: %position1%",
            "&7Position 2: %position2%",
            "",
            "&9&l[!] &9Left click to set Position 1",
            "&9&l[!] &9Right click to set Position 2"
    ));

    public Configuration() {
        super("&9", "Town", "IridiumTowns");
    }
}
