package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentCamerasBinding
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.ViewModelFactory
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindImageView
import org.koin.android.ext.android.inject

class CamerasFragment : BaseFragment<FragmentCamerasBinding>() {

    private val factory: ViewModelFactory by inject()
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        factory
    }

    private val adapter by lazy {
        CamerasAdapter(
            context = requireContext(),
            onClickCamera = OnClickCameraListener { camera, binding ->
                setCameraSelected(camera, binding)
            })
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentCamerasBinding = FragmentCamerasBinding.inflate(inflater, container, false)

    override fun setup4Vars() {
        super.setup4Vars()
        binding.apply {
            adapter.currentList = viewModel.getAllCameras()
            adapter.setOrder(viewModel.getLastOrder())
            camerasList.adapter = adapter
        }
        setCameraSelected(viewModel.getLastCameraSelected(), viewModel.getLastBindingItem())
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(binding.toolbarComponent?.toolbar, R.string.app_name, R.menu.menu)
    }

    override fun toolbarItemSelected(itemSelected: MenuItem) {
        super.toolbarItemSelected(itemSelected)
        when (itemSelected.itemId) {
            R.id.order_icon -> {
                when (adapter.getOrder()) {
                    NORMAL -> {
                        adapter.setOrder(ASCENDING)
                        viewModel.setLastOrder(ASCENDING)
                    }
                    ASCENDING -> {
                        adapter.setOrder(DESCENDING)
                        viewModel.setLastOrder(DESCENDING)
                    }
                    DESCENDING -> {
                        adapter.setOrder(ASCENDING)
                        viewModel.setLastOrder(ASCENDING)
                    }
                }
            }
            R.id.show_all -> {
                itemSelected.isChecked = !itemSelected.isChecked
            }
        }
    }

    private fun setCameraSelected(lastCamera: Camera?, lastBindingItem: ListViewItemBinding?) {
        if (lastCamera != null) {
            adapter.setLastCameraSelected(camera = lastCamera)
            adapter.setLastBindingItem(binding = lastBindingItem)
            viewModel.setLastCameraSelected(camera = lastCamera)
            viewModel.setLastBindingItem(binding = lastBindingItem)
            binding.bindImageView(imgUrl = lastCamera.url)
        }
    }

}