package com.iridium.iridiumtowns.database;

import com.iridium.iridiumteams.database.IridiumUser;

import java.util.UUID;

public class User extends IridiumUser<Town> {
    public User(UUID uuid, String name){
        setUuid(uuid);
        setName(name);
    }
}
