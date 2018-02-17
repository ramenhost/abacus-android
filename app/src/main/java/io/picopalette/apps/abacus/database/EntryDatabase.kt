package io.picopalette.apps.abacus.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by ramkumar on 16/02/18.
 */

@Database(entities = arrayOf(CheckInEntry::class), version = 1)
abstract class EntryDatabase: RoomDatabase() {

    abstract fun checkInEntryDao(): CheckInEntryDao

    companion object {
        private var INSTANCE: EntryDatabase? = null

        fun getInstance(context: Context): EntryDatabase? {
            if (INSTANCE == null) {
                synchronized(EntryDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            EntryDatabase::class.java, "entry.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}