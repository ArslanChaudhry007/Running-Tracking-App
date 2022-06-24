package com.androiddevs.runningappyt.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converts::class)
abstract class RunnigDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDAO

}