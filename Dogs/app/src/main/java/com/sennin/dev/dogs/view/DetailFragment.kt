package com.sennin.dev.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.sennin.dev.dogs.R
import com.sennin.dev.dogs.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel
    private var dogUuid = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        detailViewModel.fetch()

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        observeViewModel()

    }

    fun observeViewModel() {
        detailViewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                dogName.text = it.dogBreed
                dogPurpose.text = it.bredFor
                dogTemperament.text = it.temperament
                dogLifeSpan.text = it.lifeSpan
            }
        })
    }

}
