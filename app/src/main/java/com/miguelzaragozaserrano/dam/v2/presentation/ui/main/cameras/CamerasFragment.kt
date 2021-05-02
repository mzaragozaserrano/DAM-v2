package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.models.AdapterState
import com.miguelzaragozaserrano.dam.v2.data.models.Camera
import com.miguelzaragozaserrano.dam.v2.data.models.SearchViewState
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentCamerasBinding
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.utils.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.ALL
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.FAVORITE
import org.koin.android.ext.android.inject

class CamerasFragment : BaseFragment<FragmentCamerasBinding>() {

    private val factory: ViewModelFactory by inject()
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        factory
    }

    private val adapter by lazy {
        CamerasAdapter(
            context = requireContext(),
            fragmentBinding = binding,
            onItemClicked = OnClickItemListView { camera, binding, _ ->
                setCameraSelected(camera, binding)
            },
            onFavButtonClicked = OnClickItemListView { camera, _, favorite ->
                setCameraFavorite(camera, favorite)
            })
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentCamerasBinding = FragmentCamerasBinding.inflate(inflater, container, false)

    override fun setup2Listeners() {
        super.setup2Listeners()
        binding.cameraImage.apply {
            setOnClickListener {
                goToMapFragment()
            }
        }
    }

    override fun setup4Vars() {
        super.setup4Vars()
        binding.apply {
            bindAdapter(viewModel, camerasList, adapter)
        }
    }

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(
            toolbar = binding.toolbarComponent.toolbar,
            titleId = R.string.cameras_fragment_title,
            menuId = R.menu.menu,
            navigationIdIcon = null,
            functionOnCreateOptionsMenu = { menu -> bindToolbar(menu) }
        )
    }

    private fun bindToolbar(menu: Menu) {
        with(viewModel.searchViewState) {
            if (focus) {
                val itemSearch = menu.findItem(R.id.search_icon)
                itemSearch.bindSearch(
                    menu = menu,
                    adapter = adapter,
                    context = requireContext(),
                    searchViewState = this
                )
            }
        }
        menu.findItem(R.id.show_all).isChecked = viewModel.settingsMapState.showAll
    }

    override fun toolbarItemSelected(itemSelected: MenuItem, menu: Menu) {
        super.toolbarItemSelected(itemSelected, menu)
        when (itemSelected.itemId) {
            R.id.order_icon -> {
                when (adapter.order) {
                    NORMAL, DESCENDING -> {
                        menu.findItem(R.id.order_icon).icon =
                            getDrawable(requireContext(), R.drawable.ic_ascending_order)
                        adapter.setListByOrder(ASCENDING)
                        viewModel.adapterState.order = ASCENDING
                    }
                    ASCENDING -> {
                        menu.findItem(R.id.order_icon).icon =
                            getDrawable(requireContext(), R.drawable.ic_descending_order)
                        adapter.setListByOrder(DESCENDING)
                        viewModel.adapterState.order = DESCENDING
                    }
                }
            }
            R.id.show_all -> {
                itemSelected.isChecked = !itemSelected.isChecked
                if (itemSelected.isChecked) {
                    showMessage()
                    viewModel.settingsMapState.showAll = true
                    viewModel.settingsMapState.cameras = viewModel.allCameras
                }
                else {
                    viewModel.adapterState.camera?.let { camera ->
                        viewModel.settingsMapState.showAll = false
                        viewModel.settingsMapState.cameras = listOf(camera)
                    }
                }
            }
            R.id.recharge -> {
                viewModel.isRechargeRequest = true
                viewModel.adapterState = AdapterState()
                viewModel.searchViewState = SearchViewState()
                findNavController().navigate(R.id.action_cameras_fragment_to_splash_fragment)
            }
            R.id.search_icon -> {
                itemSelected.bindSearch(
                    menu = menu,
                    adapter = adapter,
                    context = requireContext(),
                    searchViewState = viewModel.searchViewState
                )
            }
            R.id.fav_icon -> {
                when (adapter.type) {
                    ALL -> {
                        menu.findItem(R.id.fav_icon).icon =
                            getDrawable(requireContext(), R.drawable.ic_favorite_on)
                        adapter.setListByType(FAVORITE)
                        viewModel.adapterState.type = FAVORITE
                    }
                    FAVORITE -> {
                        menu.findItem(R.id.fav_icon).icon =
                            getDrawable(requireContext(), R.drawable.ic_favorite_off)
                        adapter.setListByType(ALL)
                        viewModel.adapterState.type = ALL
                    }
                }
            }
        }
    }

    private fun showMessage() {
        showDialogMessageComplete(
            title = getString(R.string.cluster_title),
            message = getString(R.string.cluster_message),
            positiveText = getString(R.string.with_cluster_button),
            negativeText = getString(R.string.without_cluster_button),
            icon = null,
            functionPositiveButton = {
                viewModel.settingsMapState.cluster = true
            },
            functionNegativeButton = {
                viewModel.settingsMapState.cluster = false
            })
    }

    private fun setCameraSelected(camera: Camera?, bindingItem: ListViewItemBinding?) {
        if (camera != null && bindingItem != null) {
            viewModel.adapterState.camera = camera
            viewModel.adapterState.bindingItem = bindingItem
            viewModel.settingsMapState.cameras = listOf(camera)
            binding.bindImageView(imgUrl = camera.url)
        }
    }

    private fun setCameraFavorite(camera: Camera, favorite: Boolean) {
        viewModel.setCameraFavorite(camera, favorite)
    }

    private fun goToMapFragment() {
        findNavController().navigate(R.id.action_cameras_fragment_to_map_fragment)
    }

}