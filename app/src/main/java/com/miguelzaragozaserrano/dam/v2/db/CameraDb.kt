package com.miguelzaragozaserrano.dam.v2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.miguelzaragozaserrano.dam.v2.db.dao.CameraDao
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity


@Database(
    entities = [CameraEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CameraDb : RoomDatabase() {

    abstract val cameraDao: CameraDao

    companion object {

        @Volatile
        private var INSTANCE: CameraDb? = null

        fun getInstance(context: Context): CameraDb {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        CameraDb::class.java,
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