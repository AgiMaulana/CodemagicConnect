package io.github.agimaulana.codemagicconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.agimaulana.codemagicconnect.data.local.entity.AuthTokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthTokenDao {

    @Query("SELECT * FROM auth_token WHERE id = 0 LIMIT 1")
    fun observe(): Flow<AuthTokenEntity?>

    @Query("SELECT * FROM auth_token WHERE id = 0 LIMIT 1")
    suspend fun get(): AuthTokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AuthTokenEntity)

    @Query("DELETE FROM auth_token")
    suspend fun delete()
}
