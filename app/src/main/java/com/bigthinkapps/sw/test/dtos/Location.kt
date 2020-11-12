package com.bigthinkapps.sw.test.dtos

import com.bigthinkapps.sw.test.models.Coordinate
import com.bigthinkapps.sw.test.models.TimeZone

class Location {
    var street: Street? = null
    var city: String = ""
    var state: String = ""
    var postcode: String = ""
    var coordinates: Coordinate? = null
    var timezone: TimeZone? = null

    fun getAddress() = "${street?.name?.capitalize()}, ${city.capitalize()}, ${state.capitalize()}."
}