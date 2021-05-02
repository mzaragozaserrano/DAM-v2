package com.miguelzaragozaserrano.dam.v2.db.repositories

import androidx.lifecycle.LiveData
import com.miguelzaragozaserrano.dam.v2.db.dao.CameraDao
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity

class CameraRepository(private val cameraDao: CameraDao) {

    suspend fun insert(camera: CameraEntity) = cameraDao.insert(camera)

    suspend fun clearDatabase() = cameraDao.clearDatabase()

    suspend fun updateFavorite(id: String, favorite: Boolean) =
        cameraDao.updateFavorite(id, favorite)

    fun getAllCameras(): LiveData<List<CameraEntity>> = cameraDao.getAllCameras()

}