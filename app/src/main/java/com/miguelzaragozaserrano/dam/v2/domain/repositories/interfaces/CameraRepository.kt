package com.miguelzaragozaserrano.dam.v2.domain.repositories.interfaces

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.miguelzaragozaserrano.dam.v2.db.dao.CameraDao
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.domain.models.DatabaseResponse
import com.miguelzaragozaserrano.dam.v2.domain.models.Result

class CameraRepository(private val cameraDao: CameraDao) {

    @WorkerThread
    suspend fun insert(camera: CameraEntity): Result<DatabaseResponse> {
        cameraDao.insert(camera)
        return Result.Success(DatabaseResponse("OK"))
    }

    fun getAllCameras(): LiveData<List<CameraEntity>> = cameraDao.getAllCameras()

    @WorkerThread
    suspend fun getCameraById(id: String): CameraEntity? = cameraDao.getCameraById(id)

}