package com.bigthinkapps.sw.test.repositories.remote

import com.bigthinkapps.sw.test.repositories.remote.core.RetrofitClient
import com.bigthinkapps.sw.test.repositories.remote.services.RandomUsersRemoteService


object RemoteServiceFactory {
    fun getRandomUsersService() = getRetrofit()?.create(RandomUsersRemoteService::class.java)
    private fun getRetrofit() = RetrofitClient.getClient("https://randomuser.me")
}