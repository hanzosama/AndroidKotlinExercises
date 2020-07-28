package com.sennin.dev.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sennin.dev.dogs.model.DogBreed

class ListViewModel : ViewModel() {
    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val dog1 = DogBreed("1", "Gorgi", "15 years", "breedGroup", "breedFor", "yem", "te")
        val dog2 = DogBreed("2", "Labrador", "10 years", "breedGroup", "breedFor", "yem", "te")
        val dog3 = DogBreed("3", "Rotwailer", "20 years", "breedGroup", "breedFor", "yem", "te")
        val dogList = arrayListOf<DogBreed>(dog1, dog2, dog3)

        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }
}