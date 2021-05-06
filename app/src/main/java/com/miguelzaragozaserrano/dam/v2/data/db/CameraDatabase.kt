package com.miguelzaragozaserrano.dam.v2.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.miguelzaragozaserrano.dam.v2.data.db.dao.CameraDao
import com.miguelzaragozaserrano.dam.v2.data.db.entity.CameraEntity


@Database(
    entities = [CameraEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CameraDatabase : RoomDatabase() {

    abstract val cameraDao: CameraDao

    companion object {

        @Volatile
        private var INSTANCE: CameraDatabase? = null

        fun getInstance(context: Context): CameraDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        CameraDatabase::class.java,
                        "camera_table"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}