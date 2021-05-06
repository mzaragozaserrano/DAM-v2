package com.miguelzaragozaserrano.dam.v2.domain.models

data class SearchViewState(
    var query: String? = "",
    var focus: Boolean = false
)