package io.picopalette.apps.abacus.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by ramkumar on 16/02/18.
 */

@Dao interface CheckInEntryDao {
    @Insert
    fun add(checkInEntry: CheckInEntry)

    @Query("SELECT * from entry")
    fun getAllEntries(): List<CheckInEntry>

    @Query("SELECT aid from entry WHERE pushed = 0")
    fun getNewEntries(): List<String>

    @Query("UPDATE entry SET pushed=1 WHERE aid like :aid")
    fun markEntryPushed(aid: String)
}