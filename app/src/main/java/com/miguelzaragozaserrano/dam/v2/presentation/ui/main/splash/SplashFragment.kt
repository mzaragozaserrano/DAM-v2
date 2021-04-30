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
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.DATE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.PreferenceHelper.customPreference
import com.miguelzaragozaserrano.dam.v2.presentation.utils.PreferenceHelper.date
import org.koin.android.ext.android.inject
import java.time.LocalDateTime

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

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
                    checkIfNextDay(cameras)
                } else {
                    if (viewModel.isFileDownloaded()) {
                        prefs.date = LocalDateTime.now().toDateString()
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
    }

    private fun checkIfNextDay(cameras: List<CameraEntity>) {
        if (Utils.isNextDay(
                currentDay = LocalDateTime.now(), lastDay = prefs.date?.toDate()
            ) == false
        ) {
            showMessage(cameras)
        } else {
            viewModel.setAllCameras(cameras)
        }
    }

    private fun showMessage(cameras: List<CameraEntity>) {
        showDialogMessageComplete(
            title = getString(R.string.recharge_title),
            message = getString(R.string.recharge_message) + prefs.date?.toDate()?.toDateString(),
            positiveText = getString(R.string.recharge_button),
            negativeText = getString(R.string.cancel_button),
            icon = null,
            functionPositiveButton = {
                viewModel.getDataFromUrl()
                viewModel.setRequest(false)
            },
            functionNegativeButton = {
                viewModel.setAllCameras(cameras)
            })
    }

    private fun initActions() {
        viewModel.onGoToCamerasFragment = ::onGoToCamerasFragment
        viewModel.onUpdateProgressBar = ::onUpdateProgressBar
    }

    private fun onGoToCamerasFragment() {
        findNavController().navigate(R.id.action_splash_fragment_to_cameras_fragment)
    }

    private fun onUpdateProgressBar() {
        binding.bindProgressBar(
            camerasDownloaded = viewModel.getNumberCamerasDownloaded(),
            totalCameras = UtilsDownload.numberCameras
        )
    }

}