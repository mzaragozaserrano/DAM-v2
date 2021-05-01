package com.miguelzaragozaserrano.dam.v2.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity

@Dao
interface CameraDao {

    @Insert
    suspend fun insert(camera: CameraEntity)

    @Delete
    suspend fun remove(camera: CameraEntity)

    @Query("SELECT * from camera_table WHERE id = :id")
    suspend fun getCameraById(id: String): CameraEntity?

    @Query("DELETE FROM camera_table")
    suspend fun clearDatabase()

    @Query("SELECT * FROM camera_table")
    fun getAllCameras(): LiveData<List<CameraEntity>>

    @Query("UPDATE camera_table SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: String, favorite: Boolean)

}

