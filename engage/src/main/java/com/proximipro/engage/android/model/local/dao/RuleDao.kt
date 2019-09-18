package com.proximipro.engage.android.model.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.proximipro.engage.android.model.common.Rule

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Dao for the rules table
 */
@Dao
abstract class RuleDao : BaseDao<Rule> {

    /**
     * Retrieves all the rules from the local database
     * @return LiveData<List<Rule>>
     */
    @Query("SELECT * FROM rules")
    abstract fun getRules(): LiveData<List<Rule>>

    /**
     * Retrieves a particular rule from the local database that matches with the rule id provided
     * @param ruleId String is the id of the rule
     * @return Rule is the matched rule
     */
    @Query("SELECT * FROM rules where rule_id = :ruleId")
    abstract suspend fun getRule(ruleId: String): Rule

    /**
     * Clears the rules table by removing all the entries
     * @return Int
     */
    @Query("DELETE from rules")
    abstract suspend fun clear(): Int
}