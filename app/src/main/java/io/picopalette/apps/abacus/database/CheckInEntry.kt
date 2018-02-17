package io.picopalette.apps.abacus.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by ramkumar on 16/02/18.
 */

@Entity(tableName = "entry", indices = arrayOf(Index(value="aid", name="abacusid", unique = true)))
data class CheckInEntry(@PrimaryKey(autoGenerate = true) var id: Int?,
                        @ColumnInfo(name = "aid") var aId: String,
                        @ColumnInfo(name = "pushed") var pushed: Boolean)