package com.sennin.dev.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.sennin.dev.dogs.R
import com.sennin.dev.dogs.databinding.ItemDogBinding
import com.sennin.dev.dogs.model.DogBreed

class DogListAdapter(val dogsList: ArrayList<DogBreed>) :
    RecyclerView.Adapter<DogListAdapter.DogListViewHolder>(),DogClickListener {

    fun updateDogList(newDogList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogListViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view =
            DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dogsList.size
    }

    override fun onBindViewHolder(holder: DogListViewHolder, position: Int) {
        holder.view.dog = dogsList[position]
        holder.view.dogClickListener = this
    }

    class DogListViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)

    override fun onDogClicked(view: View, id: Int) {
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = id
        Navigation.findNavController(view).navigate(action)
    }

}