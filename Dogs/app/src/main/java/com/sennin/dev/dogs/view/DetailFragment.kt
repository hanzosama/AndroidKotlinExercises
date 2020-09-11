package com.sennin.dev.dogs.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sennin.dev.dogs.R
import com.sennin.dev.dogs.databinding.FragmentDetailBinding
import com.sennin.dev.dogs.databinding.SendSmsDialogBinding
import com.sennin.dev.dogs.model.DogBreed
import com.sennin.dev.dogs.model.DogPalette
import com.sennin.dev.dogs.model.SmsInfo
import com.sennin.dev.dogs.viewmodel.DetailViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel
    private var dogUuid = 0
    private lateinit var fragmentDetailBinding: FragmentDetailBinding
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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
                currentDog = it
                fragmentDetailBinding.dog = it

                it.imageUrl?.let {
                    setUpBackgroundColor(it)
                }
            }
        })
    }


    private fun setUpBackgroundColor(url: String) {
        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                //not required to implement
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                //This is part of palette library
                Palette.from(resource).generate { palette ->
                    val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                    val myPalette = DogPalette(intColor)
                    fragmentDetailBinding.palette = myPalette
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                //Check Permissions
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()

            }

            R.id.action_share -> {
                //Check Permissions
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check this dog breed")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${currentDog?.dogBreed} breed for ${currentDog?.bredFor}"
                )
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                AlertDialog.Builder(it).setView(dialogBinding.root)
                    .setPositiveButton("Send SMD") { dialog, which ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSmsInfo(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which ->

                    }.show()
                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSmsInfo(smsInfo: SmsInfo) {
        //Real SMS sending
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }

}
