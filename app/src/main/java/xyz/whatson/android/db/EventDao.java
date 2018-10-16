package xyz.whatson.android.db;

import xyz.whatson.android.model.Event;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events")
    List<Event> listAll();

    @Query("SELECT * FROM events ORDER BY lastModified DESC LIMIT 1")
    Event getLatest();

    @Query("SELECT * FROM events WHERE eventStartTime >= (:eventStartTime) ORDER BY eventStartTime ASC")
    List<Event> getEventsAfterTime(long eventStartTime);

    @Query("SELECT * FROM events WHERE key in (:keys) AND eventStartTime >= (:now)")
    List<Event> getStarredEvents(List<String> keys, long now);

    @Query("SELECT * FROM events WHERE owner = (:uid) AND eventStartTime >= (:now)")
    List<Event> getHostedEvents(String uid, long now);

    @Query("SELECT * FROM events WHERE key in (:keys) AND eventStartTime <= (:now)")
    List<Event> getPastEvents(List<String> keys, long now);

    @Query("SELECT * FROM events WHERE key = (:key)")
    Event searchByKey(String key);

    @Query("SELECT * FROM events WHERE (LOWER(title) LIKE LOWER(:regex) OR LOWER(description) LIKE LOWER(:regex)) AND (eventStartTime >= (:startTime) OR (:startTime) IS NULL) AND (eventEndTime <= (:endTime) OR (:endTime) IS NULL) AND category IN (:categories) ORDER BY eventStartTime ASC")
    List<Event> searchEvents(String regex, long startTime, long endTime, List<String> categories);

//    @Query("SELECT * FROM events WHERE title REGEXP (:regex) OR description REGEXP (:regex) AND eventStartTime >= (:startTime) AND category = (:category)")
//    List<Event> searchEvents(String regex, long startTime, String category);
//
//    @Query("SELECT * FROM events WHERE title REGEXP (:regex) OR description REGEXP (:regex) AND eventStartTime >= (:startTime) AND eventEndTime <= (:endTime)")
//    List<Event> searchEvents(String regex, long startTime, long endTime);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Insert
    void insertAll(Event... events);

    @Query("DELETE FROM events WHERE key = (:key)")
    void remove(String key);

    @Query("DELETE FROM events")
    void deleteAll();
}
