package com.example.data.repository

import com.example.data.local.SavedSquadEntity
import com.example.data.local.SquadDao
import com.example.data.local.UserProfileDao
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class DraftRepository(
    private val squadDao: SquadDao,
    private val userProfileDao: UserProfileDao
) {
    val allSquads: Flow<List<SavedSquadEntity>> = squadDao.getAllSquads()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun saveSquad(squad: SavedSquadEntity): Long {
        return squadDao.insertSquad(squad)
    }

    suspend fun deleteSquad(id: Int) {
        squadDao.deleteSquadById(id)
    }

    suspend fun getSquadById(id: Int): SavedSquadEntity? {
        return squadDao.getSquadById(id)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.upsertProfile(profile)
    }
}
