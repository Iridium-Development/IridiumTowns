package com.iridium.iridiumtowns.managers.tablemanagers;

import com.iridium.iridiumtowns.SortedList;
import com.iridium.iridiumtowns.database.Town;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TownTableManager extends TableManager<Town, Integer> {
    private final SortedList<Town> townEntries;

    public TownTableManager(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Town.class, Comparator.comparing(Town::getId));
        this.townEntries = new SortedList<>(Comparator.comparing(Town::getName, String.CASE_INSENSITIVE_ORDER));
        this.townEntries.addAll(getEntries());
        sort();
    }

    @Override
    public void addEntry(Town town) {
        super.addEntry(town);
        townEntries.add(town);
    }

    @Override
    public CompletableFuture<Void> delete(Town faction) {
        townEntries.remove(faction);
        return super.delete(faction);
    }

    @Override
    public CompletableFuture<Void> delete(Collection<Town> factions) {
        townEntries.removeAll(factions);
        return super.delete(factions);
    }

    /**
     * Sort the list of entries by UUID
     */
    public void sort() {
        getEntries().sort(Comparator.comparing(Town::getId));
        townEntries.sort(Comparator.comparing(Town::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public Optional<Town> getTown(int id) {
        int index = Collections.binarySearch(getEntries(), new Town(id), Comparator.comparing(Town::getId));
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    public Optional<Town> getTown(String name) {
        int index = Collections.binarySearch(townEntries, new Town(name), Comparator.comparing(Town::getName, String.CASE_INSENSITIVE_ORDER));
        if (index < 0) return Optional.empty();
        return Optional.of(townEntries.get(index));
    }
}
