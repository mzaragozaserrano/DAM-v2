package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentCamerasBinding
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.ViewModelFactory
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindAdapter
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindImageView
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindSearch
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
            bindAdapter(viewModel, camerasList, adapter)
        }
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(binding.toolbarComponent.toolbar, R.string.app_name, R.menu.menu)
    }

    override fun toolbarItemSelected(itemSelected: MenuItem, menu: Menu) {
        super.toolbarItemSelected(itemSelected, menu)
        when (itemSelected.itemId) {
            R.id.order_icon -> {
                when (adapter.order) {
                    NORMAL -> {
                        adapter.setOrderList(ASCENDING)
                        viewModel.lastOrder = ASCENDING
                    }
                    ASCENDING -> {
                        adapter.setOrderList(DESCENDING)
                        viewModel.lastOrder = DESCENDING
                    }
                    DESCENDING -> {
                        adapter.setOrderList(ASCENDING)
                        viewModel.lastOrder = ASCENDING
                    }
                }
            }
            R.id.show_all -> {
                itemSelected.isChecked = !itemSelected.isChecked
            }
            R.id.reset -> {
                viewModel.isResetRequest = true
                findNavController().navigate(R.id.action_cameras_fragment_to_splash_fragment)
            }
            R.id.search_icon -> {
                itemSelected.bindSearch(menu, adapter, requireContext())
            }
        }
    }

    private fun setCameraSelected(lastCamera: Camera?, lastBindingItem: ListViewItemBinding?) {
        if (lastCamera != null && lastBindingItem != null) {
            viewModel.lastCameraSelected = lastCamera
            viewModel.lastBindingItem = lastBindingItem
            binding.bindImageView(imgUrl = lastCamera.url)
        }
    }

}