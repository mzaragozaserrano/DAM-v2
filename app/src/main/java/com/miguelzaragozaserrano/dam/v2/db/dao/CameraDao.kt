package com.miguelzaragozaserrano.dam.v2.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import io.reactivex.rxjava3.core.Completable

@Dao
interface CameraDao {

    @Insert
    suspend fun insert(camera: CameraEntity)

    @Delete
    suspend fun remove(camera: CameraEntity)

    @Query("SELECT * from camera_table WHERE id = :id")
    suspend fun getCameraById(id: String): CameraEntity?

    /*@Query("UPDATE camera_table SET selected = :selected")
    suspend fun updateSelected(selected: Boolean)*/

    @Query("DELETE FROM camera_table")
    suspend fun clear()

    @Query("SELECT * FROM camera_table")
    fun getAllCameras(): LiveData<List<CameraEntity>>

}

