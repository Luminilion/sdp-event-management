package ch.epfl.sweng.eventmanager.room.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface JoinedEventDao {
    @Query("SELECT * FROM joined_events")
    LiveData<List<JoinedEvent>> getAll();

    @Query("SELECT * FROM joined_events WHERE event_id IN (:eventIds)")
    LiveData<List<JoinedEvent>> loadAllByIds(int[] eventIds);

    @Query("SELECT * FROM joined_events WHERE name LIKE :eventName LIMIT 1")
    LiveData<JoinedEvent> findByName(String eventName);

    @Query("SELECT * FROM joined_events WHERE event_id LIKE :eventId LIMIT 1")
    LiveData<JoinedEvent> findById(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JoinedEvent joinedEvent);

    @Delete
    int delete(JoinedEvent joinedEvent);
}
