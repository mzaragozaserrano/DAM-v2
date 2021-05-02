package com.miguelzaragozaserrano.dam.v2.data.dto.response

class GoogleMapsResponse {
    var routes:ArrayList<Routes>? = null
}

class Routes{
    var legs:ArrayList<Legs>? = null
}

class Legs{
    var steps:ArrayList<Steps>? = null
}

class Steps{
    var polyline: Polyline? = null
}

class Polyline{
    var points:String = ""
}