package com.bigthinkapps.sw.test.repositories.local.services

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bigthinkapps.sw.test.models.Contact

@Dao
interface ContactDao {
    @Insert
    fun insert(contact: Contact)

    @Update
    fun update(vararg contact: Contact)

    @Delete
    fun delete(vararg contact: Contact)

    @Query("SELECT * FROM " + Contact.TABLE_NAME + " ORDER BY last_name, first_name")
    fun getOrderedAgenda(): LiveData<List<Contact>>
}