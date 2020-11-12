package com.bigthinkapps.sw.test.repositories.local

import android.content.Context
import com.bigthinkapps.sw.test.repositories.local.core.DatabaseContext
import com.bigthinkapps.sw.test.repositories.local.services.FavoriteUsersLocalService
import com.bigthinkapps.sw.test.repositories.local.services.RandomUsersLocalService

object LocalServiceFactory {

    fun getRandomUsersService(ctx: Context): RandomUsersLocalService {
        return DatabaseContext.getInstance(ctx).getRandomUserLocalService()
    }

    fun getFavoriteUsersService(ctx: Context): FavoriteUsersLocalService {
        return DatabaseContext.getInstance(ctx).getFavoriteUsersLocalService()
    }
}