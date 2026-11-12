package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY createdAt DESC")
    fun getAllPeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE type = :type ORDER BY createdAt DESC")
    fun getPeopleByType(type: String): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    fun getPersonById(id: Long): Flow<PersonEntity?>

    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    suspend fun getPersonByIdSync(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(people: List<PersonEntity>)

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun deletePersonById(id: Long)

    @Query("DELETE FROM people")
    suspend fun deleteAll()

    @Query("SELECT * FROM people ORDER BY id ASC")
    suspend fun getAllPeopleSync(): List<PersonEntity>
}
