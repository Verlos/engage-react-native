package com.proximipro.engage.android.model.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Base class for creating DAOs for the local database
 * @param T is the entity type for the dao
 */
interface BaseDao<T> {

    /**
     * inserts a single item into the database
     * @param item T is the item that will be inserted into the database
     * @return Long that determines whether the item is entered in the database or not
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T): Long

    /**
     * Inserts a list of items into the local database
     * @param items List<T> is the item list that will be added to the database
     * @return Array<Long> that determines whether the items are added into the database or not
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<T>): Array<Long>

    /**
     * Deletes the given item from the database
     * @param item T is the item that will be deleted from the database
     * @return Int used to determine whether the item is deleted or not
     */
    @Delete
    suspend fun delete(item: T): Int
}