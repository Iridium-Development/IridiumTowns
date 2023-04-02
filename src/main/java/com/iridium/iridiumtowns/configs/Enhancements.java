package com.iridium.iridiumtowns.configs;

public class Enhancements extends com.iridium.iridiumteams.configs.Enhancements {

    public Enhancements(){
        super("&2");
        this.membersEnhancement.item.slot = 9;
        this.warpsEnhancement.item.slot = 11;
        this.potionEnhancements.get("haste").item.slot = 13;
        this.potionEnhancements.get("speed").item.slot = 15;
        this.potionEnhancements.get("jump").item.slot = 17;
    }

}
