package com.iridium.iridiumtowns.managers.tablemanagers;

import com.iridium.iridiumtowns.SortedList;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TableManager<T, S> {
    private final SortedList<T> entries;
    private final Dao<T, S> dao;
    private final Class<T> clazz;

    private final ConnectionSource connectionSource;

    public TableManager(ConnectionSource connectionSource, Class<T> clazz, Comparator<T> comparator) throws SQLException {
        this.connectionSource = connectionSource;
        this.entries = new SortedList<>(comparator);
        TableUtils.createTableIfNotExists(connectionSource, clazz);
        this.dao = DaoManager.createDao(connectionSource, clazz);
        this.dao.setAutoCommit(getDatabaseConnection(), false);
        this.entries.addAll(dao.queryForAll());
        this.clazz = clazz;
    }

    public void save() {
        try {
            List<T> entryList = new ArrayList<>(entries);
            for (T t : entryList) {
                dao.createOrUpdate(t);
            }
            dao.commit(getDatabaseConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addEntry(T t) {
        entries.add(t);
    }

    public List<T> getEntries() {
        return entries;
    }

    public CompletableFuture<Void> delete(T t) {
        entries.remove(t);
        return CompletableFuture.runAsync(() -> {
            try {
                dao.delete(t);
                dao.commit(getDatabaseConnection());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> delete(Collection<T> t) {
        entries.removeAll(t);
        return CompletableFuture.runAsync(() -> {
            try {
                dao.delete(t);
                dao.commit(getDatabaseConnection());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    private DatabaseConnection getDatabaseConnection() throws SQLException {
        return connectionSource.getReadWriteConnection(null);
    }

    public Dao<T, S> getDao() {
        return dao;
    }
}
