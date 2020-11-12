package com.bigthinkapps.sw.test.models

class TimeZone {
    var offset: String = "0:00"
    var description: String = ""

    fun getFormattedOffset() = "GMT$offset"
    fun getFullTimeZone() = "$description. ${getFormattedOffset()}"
}