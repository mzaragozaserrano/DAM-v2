package com.miguelzaragozaserrano.dam.v2.data.models

import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.*

data class AdapterState(
    var order: Constants.ORDER = NORMAL,
    var type: Constants.TYPE = ALL,
    var camera: Camera? = null,
    var bindingItem: ListViewItemBinding? = null
)