package com.iridium.iridiumtowns.configs;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";
    public boolean ignoreYSpace = true;

    public Configuration() {
        super("&9", "Town", "IridiumTowns");
    }
}
