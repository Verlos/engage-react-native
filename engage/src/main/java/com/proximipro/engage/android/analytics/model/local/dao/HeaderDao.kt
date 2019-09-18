package com.proximipro.engage.android.analytics.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.proximipro.engage.android.analytics.model.local.entity.Header
import com.proximipro.engage.android.model.local.dao.BaseDao

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Dao for Header table
 */
@Dao
abstract class HeaderDao : BaseDao<Header> {

    /**
     * Retrieves header from the headers table matching with given ID
     * @param id String is the id for which the header will be retrieved
     * @return Header whose id matches with the given ID
     */
    @Query("SELECT * FROM header WHERE id=:id")
    abstract suspend fun getHeader(id: String): Header

    /**
     * Retrieves all the headers from the headers table
     * @return List<Header> is the list of retrieved headers
     */
    @Query("SELECT * FROM Header")
    abstract suspend fun getAllHeaders(): List<Header>
}