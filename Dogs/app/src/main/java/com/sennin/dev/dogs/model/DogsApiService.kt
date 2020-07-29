package com.sennin.dev.dogs.model

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DogsApiService {
    private val BASE_URL = "https://raw.githubusercontent.com/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        // Convert  JSON object to model using GSON
        .addConverterFactory(GsonConverterFactory.create())
        // this convert the data model to Observables formats.
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        // create the services using the interface definition of the API
        .create(DogsApi::class.java)

    fun getDogs(): Single<List<DogBreed>> {
        return api.getDogs()
    }
}