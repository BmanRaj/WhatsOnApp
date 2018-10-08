package xyz.whatson.android.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import xyz.whatson.android.model.DateTypeConverter;
import xyz.whatson.android.model.Event;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class WhatsonDB extends RoomDatabase {
    private static final String DATABASE_NAME = "whatson_db";
    private static WhatsonDB DBINSTANCE;

    public abstract EventDao eventDao();

    public static WhatsonDB getDatabase(Context context) {
        if (DBINSTANCE == null) {
            synchronized (WhatsonDB.class) {
                DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        WhatsonDB.class, DATABASE_NAME).build();
            }
        }
        return DBINSTANCE;
    }

    public static void destroyInstance() {
        DBINSTANCE = null;
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }
}
