package com.miguelzaragozaserrano.dam.v2.presentation.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.db.CameraDb
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.domain.models.DatabaseResponse
import com.miguelzaragozaserrano.dam.v2.domain.models.Result
import com.miguelzaragozaserrano.dam.v2.domain.repositories.interfaces.CameraRepository
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.UtilsData
import com.miguelzaragozaserrano.dam.v2.presentation.utils.toCameraEntity
import com.miguelzaragozaserrano.dam.v2.presentation.utils.toListCamera
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : BaseViewModel() {

    private val database by lazy { CameraDb.getInstance(context) }
    private val repository by lazy { CameraRepository(database.cameraDao) }

    private var newRequest = true
    private var allCameras = mutableListOf<Camera>()
    private var camerasDownloaded = 0
    private var lastCameraSelected: Camera? = null
    private var lastBindingItem: ListViewItemBinding? = null

    var camerasLiveData: LiveData<List<CameraEntity>> = repository.getAllCameras()

    lateinit var onGoToCamerasFragment: () -> Unit
    lateinit var onUpdateProgressBar: () -> Unit

    fun getDataFromUrl() {
        UtilsData.downloadFile()
        UtilsData.onCameraDownload = { camera ->
            insert(camera = camera)
        }
    }

    fun getAllCameras(): List<Camera> = allCameras

    fun getNumberCamerasDownloaded(): Int = camerasDownloaded

    fun getLastCameraSelected(): Camera? = lastCameraSelected

    fun getLastBindingItem(): ListViewItemBinding? = lastBindingItem

    fun setLastCameraSelected(camera: Camera) {
        lastCameraSelected = camera
    }

    fun setLastBindingItem(binding: ListViewItemBinding?) {
        lastBindingItem = binding
    }

    fun setAllCameras(cameras: List<CameraEntity>) {
        allCameras.addAll(cameras.toListCamera())
        onGoToCamerasFragment.invoke()
    }

    fun isNewRequest(): Boolean = newRequest

    fun setRequest(status: Boolean) {
        newRequest = status
    }

    fun isFileDownloaded(): Boolean = camerasDownloaded == (UtilsData.numberCameras?.minus(1))

    private fun insert(camera: Camera) = viewModelScope.launch {
        when (repository.insert(camera = camera.toCameraEntity())) {
            is Result.Success<DatabaseResponse> -> {
                camerasDownloaded++
                onUpdateProgressBar.invoke()
            }
            else -> Log.d("hola", "error")
        }
    }

}