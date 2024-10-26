package com.iridium.iridiumtowns.configs;

public class Messages extends com.iridium.iridiumteams.configs.Messages {

    public String givenClaimWand = "%prefix% &7You have been given a claim wand.";
    public String notHoldingClaimWand = "%prefix% &7You are not holding a claim wand.";
    public String position1NotSet = "%prefix% &7Position 1 has not been set.";
    public String position2NotSet = "%prefix% &7Position 2 has not been set.";
    public String position1Set = "%prefix% &7Position 1 has been set.";
    public String position2Set = "%prefix% &7Position 2 has been set.\n%prefix% &7Do /town claim confirm to confirm your claim";
    public String claimSet = "%prefix% &7Your claim has been created.";
    public String claimOverlap = "%prefix% &7This claim overlaps a pre existing claim.";

    public Messages() {
        super("Town", "t", "IridiumTowns", "&2");
    }
}
