package com.sennin.dev.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.sennin.dev.dogs.R
import com.sennin.dev.dogs.databinding.FragmentDetailBinding
import com.sennin.dev.dogs.databinding.ItemDogBinding
import com.sennin.dev.dogs.util.getProgressDrawable
import com.sennin.dev.dogs.util.loadImage
import com.sennin.dev.dogs.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.item_dog.view.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel
    private var dogUuid = 0
    private lateinit var fragmentDetailBinding: FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentDetailBinding =
            DataBindingUtil.inflate<FragmentDetailBinding>(
                inflater,
                R.layout.fragment_detail,
                container,
                false
            )
        return fragmentDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        detailViewModel.fetchDog(dogUuid)
        DataBindingUtil.getBinding<FragmentDetailBinding>(view)
        observeViewModel()

    }

    fun observeViewModel() {
        detailViewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                fragmentDetailBinding.dog = it
            }
        })
    }

}
