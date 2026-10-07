package io.github.agimaulana.codemagicconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.agimaulana.codemagicconnect.data.local.entity.PreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {

    @Query("SELECT * FROM preference")
    fun observeAll(): Flow<List<PreferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PreferenceEntity)

    @Query("DELETE FROM preference WHERE key = :key")
    suspend fun delete(key: String)
}
