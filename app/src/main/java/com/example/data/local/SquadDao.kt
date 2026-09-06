package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SquadDao {
    @Query("SELECT * FROM saved_squads ORDER BY createdAt DESC")
    fun getAllSquads(): Flow<List<SavedSquadEntity>>

    @Query("SELECT * FROM saved_squads WHERE id = :id")
    suspend fun getSquadById(id: Int): SavedSquadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSquad(squad: SavedSquadEntity): Long

    @Update
    suspend fun updateSquad(squad: SavedSquadEntity)

    @Query("DELETE FROM saved_squads WHERE id = :id")
    suspend fun deleteSquadById(id: Int)

    @Query("DELETE FROM saved_squads")
    suspend fun deleteAllSquads()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)
}
