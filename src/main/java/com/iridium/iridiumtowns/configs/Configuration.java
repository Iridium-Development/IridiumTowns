package com.iridium.iridiumtowns.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;

import java.util.Arrays;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";
    public String defaultDescription = "Default town description :c";
    public boolean ignoreYSpace = true;
    public Item claimWand = new Item(XMaterial.GOLDEN_AXE, 1, "&2&lClaim Wand", Arrays.asList(
            "&7Position 1: %position1%",
            "&7Position 2: %position2%",
            "",
            "&2&l[!] &2Left click to set Position 1",
            "&2&l[!] &2Right click to set Position 2"
    ));
    public String townTitleTop = "&2%town_name%";
    public String townTitleBottom = "&7%town_description%";

    public Configuration() {
        super("&2", "Town", "IridiumTowns");
    }
}
