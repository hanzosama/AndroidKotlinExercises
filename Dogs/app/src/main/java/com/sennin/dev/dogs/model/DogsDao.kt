package com.sennin.dev.dogs.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogsDao {

    //suspend <- due to it need to be in a separate thread
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query(value = "SELECT * FROM dogbreed ")
    suspend fun getAllDogs(): List<DogBreed>

    @Query(value = "SELECT * FROM dogbreed WHERE uuid = :dogId")
    suspend fun getDog(dogId: Int): DogBreed

    @Query(value = "DELETE FROM DogBreed")
    suspend fun deleteAllDogs()
}