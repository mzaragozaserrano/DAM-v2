package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentSplashBinding
import com.miguelzaragozaserrano.dam.v2.data.db.entity.CameraEntity
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.NEW_APP
import com.miguelzaragozaserrano.dam.v2.presentation.utils.PreferenceHelper.date
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.isNextDay
import com.miguelzaragozaserrano.dam.v2.presentation.utils.UtilsDownload.numberCameras
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import kotlin.system.exitProcess

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val factory: ViewModelFactory by inject()
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        factory
    }

    private val dbListCamerasObserver: Observer<List<CameraEntity>> by lazy {
        Observer { cameras ->
            when {
                cameras.size == 300 && viewModel.isRechargeRequest -> {
                    viewModel.clearDatabase()
                }
                viewModel.isFirstTime && prefs.date != NEW_APP -> {
                    checkIfNextDay(cameras)
                }
                viewModel.isFileDownloaded() && !viewModel.isRechargeRequest -> {
                    prefs.date = LocalDateTime.now().toDateString()
                    goToCamerasFragment(cameras)
                }
                cameras.isEmpty() -> {
                    getDataFromUrl()
                }
                else -> {
                    viewModel.isRechargeRequest = false
                    binding.bindProgressBar(
                        camerasDownloaded = cameras.size,
                        totalCameras = numberCameras
                    )
                }
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)

    override fun setup1Observers() {
        super.setup1Observers()
        if (!viewModel.dbListCameras.hasObservers()) {
            viewModel.dbListCameras.observe(viewLifecycleOwner, dbListCamerasObserver)
        }
    }

    override fun setup4Vars() {
        super.setup4Vars()
        binding.bindProgressCircle()
    }

    private fun checkIfNextDay(cameras: List<CameraEntity>) {
        viewModel.isFirstTime = false
        if (isNextDay(
                currentDay = LocalDateTime.now(), lastDay = prefs.date?.toDate()
            ) == true
        ) {
            showMessage(cameras)
        } else {
            goToCamerasFragment(cameras)
        }
    }

    private fun goToCamerasFragment(cameras: List<CameraEntity>) {
        viewModel.allCameras.clear()
        viewModel.allCameras.addAll(cameras.toListCamera())
        findNavController().navigate(R.id.action_splash_fragment_to_cameras_fragment)
    }

    private fun getDataFromUrl() {
        if (isNetworkAvailable()) {
            viewModel.isFirstTime = false
            viewModel.getDataFromUrl()
        } else {
            showError()
        }
    }

    private fun showMessage(cameras: List<CameraEntity>) {
        showDialogMessageComplete(
            title = getString(R.string.recharge_title),
            message = getString(R.string.recharge_message) + " " + prefs.date?.toDate()
                ?.toDateString(),
            positiveText = getString(R.string.recharge_button),
            negativeText = getString(R.string.cancel_button),
            icon = R.drawable.ic_recharge,
            functionPositiveButton = {
                viewModel.clearDatabase()
            },
            functionNegativeButton = {
                goToCamerasFragment(cameras)
            })
    }

    private fun showError() {
        showDialogMessageComplete(
            title = getString(R.string.network_title),
            message = getString(R.string.network_message),
            positiveText = getString(R.string.retry_button),
            negativeText = getString(R.string.cancel_button),
            icon = R.drawable.ic_network,
            functionPositiveButton = {
                getDataFromUrl()
            },
            functionNegativeButton = {
                exitProcess(0)
            }
        )
    }

}