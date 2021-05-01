package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentMapBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.base.BaseFragment

class MapFragment : BaseFragment<FragmentMapBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

    override fun setup5InitFunctions() {
        super.setup5InitFunctions()
        setupToolbar(
            toolbar = binding.toolbarComponent.toolbar,
            titleId = R.string.map_fragment_title,
            menuId = null,
            navigationIdIcon = R.drawable.ic_arrow_back
        )
    }

    override fun onBackPressedFun() {
        super.onBackPressedFun()
        findNavController().navigate(R.id.action_map_fragment_to_cameras_fragment)
    }
}