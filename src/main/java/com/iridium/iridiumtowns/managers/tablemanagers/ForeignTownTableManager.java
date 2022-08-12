package com.iridium.iridiumtowns.managers.tablemanagers;

import com.iridium.iridiumteams.database.TeamData;
import com.iridium.iridiumtowns.SortedList;
import com.iridium.iridiumtowns.database.Town;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ForeignTownTableManager<T extends TeamData, S> extends TableManager<T, S> {

    private final Comparator<T> comparator;
    private final SortedList<T> townSortedList;

    public ForeignTownTableManager(ConnectionSource connectionSource, Class<T> clazz, Comparator<T> comparator) throws SQLException {
        super(connectionSource, clazz, comparator);
        this.comparator = comparator;
        this.townSortedList = new SortedList<>(Comparator.comparing(TeamData::getTeamID));
        this.townSortedList.addAll(getEntries());
        sort();
    }

    @Override
    public CompletableFuture<Void> delete(T t) {
        townSortedList.remove(t);
        return super.delete(t);
    }

    @Override
    public CompletableFuture<Void> delete(Collection<T> t) {
        townSortedList.removeAll(t);
        return super.delete(t);
    }

    @Override
    public void addEntry(T t) {
        super.addEntry(t);
        townSortedList.add(t);
    }

    public List<T> getEntries(@NotNull Town town) {
        int index = Collections.binarySearch(townSortedList, new TeamData(town), Comparator.comparing(TeamData::getTeamID));
        if (index < 0) return Collections.emptyList();

        int currentIndex = index - 1;
        List<T> result = new ArrayList<>();
        result.add(getEntries().get(index));

        while (true) {
            if (currentIndex < 0) break;
            TeamData townData = getEntries().get(currentIndex);
            if (townData == null) {
                currentIndex--;
                continue;
            }
            if (town.getId() == townData.getTeamID()) {
                result.add(getEntries().get(currentIndex));
                currentIndex--;
            } else {
                break;
            }
        }

        currentIndex = index + 1;

        while (true) {
            if (currentIndex >= getEntries().size()) break;
            TeamData townData = getEntries().get(currentIndex);
            if (townData == null) {
                currentIndex++;
                continue;
            }
            if (town.getId() == townData.getTeamID()) {
                result.add(getEntries().get(currentIndex));
                currentIndex++;
            } else {
                break;
            }
        }
        return result;
    }

    public void sort() {
        getEntries().sort(comparator);
        townSortedList.sort(Comparator.comparing(TeamData::getTeamID));
    }
}
