package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.splash

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentSplashBinding
import com.miguelzaragozaserrano.dam.v2.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.PreferenceHelper.customPreference
import com.miguelzaragozaserrano.dam.v2.presentation.utils.PreferenceHelper.date
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.util.*

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val DATE = "DATE"

    private val factory: ViewModelFactory by inject()
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        factory
    }

    private lateinit var prefs: SharedPreferences

    private val camerasObserver: Observer<List<CameraEntity>> by lazy {
        Observer { cameras ->
            if (cameras.isEmpty()) {
                viewModel.getDataFromUrl()
                viewModel.setRequest(false)
            } else {
                if (viewModel.isNewRequest()) {
                    showDialogMessageComplete(
                        title = "¿Deseas recargar los datos?",
                        message = "La última vez que se descargó el fichero fue el " + prefs.date,
                        positiveText = "Recargar",
                        negativeText = "Cancelar",
                        icon = null,
                        functionPositiveButton = {
                            viewModel.getDataFromUrl()
                            viewModel.setRequest(false)
                        },
                        functionNegativeButton = {
                            viewModel.setAllCameras(cameras)
                        })
                } else {
                    if (viewModel.isFileDownloaded()) {
                        prefs.date = LocalDateTime.now().toDateWithoutTime()
                        viewModel.setAllCameras(cameras)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = customPreference(requireContext(), DATE)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)

    override fun setup1Observers() {
        super.setup1Observers()
        if (!viewModel.camerasLiveData.hasObservers()) {
            viewModel.camerasLiveData.observe(viewLifecycleOwner, camerasObserver)
        }
    }

    override fun setup4Vars() {
        super.setup4Vars()
        binding.bindProgressCircle()
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        initActions()
        getLastDateSinceFileDownload()
    }

    private fun initActions() {
        viewModel.onGoToCamerasFragment = ::onGoToCamerasFragment
        viewModel.onUpdateProgressBar = ::onUpdateProgressBar
    }

    private fun getLastDateSinceFileDownload() {

    }

    private fun onGoToCamerasFragment() {
        findNavController().navigate(R.id.action_splash_fragment_to_cameras_fragment)
    }

    private fun onUpdateProgressBar() {
        binding.bindProgressBar(
            camerasDownloaded = viewModel.getNumberCamerasDownloaded(),
            totalCameras = UtilsData.numberCameras
        )
    }

}