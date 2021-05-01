package com.miguelzaragozaserrano.dam.v2.presentation.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.db.CameraDb
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.domain.repositories.interfaces.CameraRepository
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.UtilsDownload.downloadFile
import com.miguelzaragozaserrano.dam.v2.presentation.utils.UtilsDownload.numberCameras
import com.miguelzaragozaserrano.dam.v2.presentation.utils.UtilsDownload.onCameraDownload
import com.miguelzaragozaserrano.dam.v2.presentation.utils.toCameraEntity
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : BaseViewModel() {

    private val database by lazy { CameraDb.getInstance(context) }
    private val repository by lazy { CameraRepository(database.cameraDao) }

    var isFirstTime = true
    var isRechargeRequest = false
    var allCameras = mutableListOf<Camera>()

    var lastOrder: Constants.ORDER = NORMAL
    var lastType: Constants.TYPE = ALL
    var lastCameraSelected: Camera? = null
    var lastBindingItem: ListViewItemBinding? = null

    var dbListCameras: LiveData<List<CameraEntity>> = repository.getAllCameras()

    fun isFileDownloaded(): Boolean =
        dbListCameras.value?.size == (numberCameras?.minus(1))

    fun clearDatabase() = viewModelScope.launch {
        repository.clearDatabase()
    }

    fun getDataFromUrl() {
        downloadFile()
        onCameraDownload = { camera ->
            insert(camera = camera)
        }
    }

    fun setCameraFavorite(camera: Camera, favorite: Boolean) = viewModelScope.launch {
        repository.updateFavorite(camera.id, favorite)
    }

    private fun insert(camera: Camera) = viewModelScope.launch {
        repository.insert(camera = camera.toCameraEntity())
    }

}