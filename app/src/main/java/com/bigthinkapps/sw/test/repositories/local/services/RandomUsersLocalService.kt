package com.bigthinkapps.sw.test.repositories.local.services

import androidx.room.Dao
import androidx.room.Query
import com.bigthinkapps.sw.test.models.FavoriteUser
import com.bigthinkapps.sw.test.models.User
import com.bigthinkapps.sw.test.repositories.local.core.BaseDAO

@Dao
abstract class RandomUsersLocalService : BaseDAO<User>() {
    @Query("SELECT COUNT(*) FROM Users")
    abstract fun getUsersCount(): Int

    @Query("SELECT u.* FROM Users AS u LEFT JOIN FavoriteUsers AS f ON u.id = f.userId WHERE f.id IS NULL LIMIT :limit OFFSET IFNULL(:offset, 0)")
    abstract fun getUsers(limit: Int = 50, offset: Int? = null): List<User>

    @Query("SELECT * FROM Users WHERE id = :id LIMIT 1")
    abstract fun getUserById(id: Int): User
}

@Dao
abstract class FavoriteUsersLocalService : BaseDAO<FavoriteUser>() {
    @Query("SELECT u.* FROM Users AS u, FavoriteUsers AS f WHERE u.id = f.userId")
    abstract fun getAllFavoriteUsers(): List<User>
}