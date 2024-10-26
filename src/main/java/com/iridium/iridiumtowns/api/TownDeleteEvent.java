package com.iridium.iridiumtowns.api;

import com.iridium.iridiumtowns.database.Town;
import com.iridium.iridiumtowns.database.User;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class TownDeleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @NotNull private final Town town;
    @NotNull private final User user;

    public TownDeleteEvent(@NotNull Town town, @NotNull User user) {
        this.town = town;
        this.user = user;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}