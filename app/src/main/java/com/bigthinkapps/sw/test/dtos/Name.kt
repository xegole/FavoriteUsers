package com.bigthinkapps.sw.test.dtos

class Name {
    var title: String = ""
    var first: String = ""
    var last: String = ""

    fun getFullName() = "${first.capitalize()} ${last.capitalize()}"
}