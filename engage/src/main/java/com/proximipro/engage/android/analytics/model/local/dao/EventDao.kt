package com.proximipro.engage.android.analytics.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.proximipro.engage.android.analytics.model.local.entity.Event
import com.proximipro.engage.android.model.local.dao.BaseDao

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Dao for Event table
 */
@Dao
abstract class EventDao : BaseDao<Event> {

    /**
     * Retrieves an event from the events table matching with the given id
     * @param id String is the event id for which the event will be retrieved
     * @return Event whose id matches with the given id
     */
    @Query("SELECT * FROM event WHERE id = :id")
    abstract suspend fun getEvent(id: String): Event

    /**
     * Retrieves all the events stored in events table
     * @return List<Event> is the list of all the stored events
     */
    @Query("SELECT * FROM Event")
    abstract suspend fun getAllEvents(): List<Event>

    /**
     * Retrieves events having same header
     * @param id String is the header id which will be matched
     * @return List<Event> that contains all the matched events with given header ID
     */
    @Query("SELECT * FROM Event WHERE headerID=:id")
    abstract suspend fun getHeaderEvents(id: String): List<Event>

    /**
     * Removes events having the same provided header ID
     * @param id String is the header ID for which all the events will be removed
     */
    @Query("DELETE FROM Event WHERE headerID=:id")
    abstract suspend fun deleteEventsForHeader(id: String)
}