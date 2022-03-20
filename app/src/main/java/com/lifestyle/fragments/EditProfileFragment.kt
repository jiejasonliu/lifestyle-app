package com.lifestyle.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lifestyle.R
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.lifestyle.databinding.FragmentEditProfileBinding
import com.lifestyle.helpers.ExternalStorageSaver
import com.lifestyle.interfaces.IUserProfile
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.viewmodels.ProfileFormViewModel
import com.lifestyle.viewmodels.UserViewModel

class EditProfileFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentEditProfileBinding

    // activityViewModels(): fragment should share view models with the activity that created it
    private val userViewModel: UserViewModel by activityViewModels()
    private val profileFormViewModel: ProfileFormViewModel by activityViewModels()

    companion object {
        const val ICON_WIDTH: Int = 128
        const val ICON_HEIGHT: Int = 128
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        binding.viewModel = profileFormViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind observers from view models
        bindObservers()

        // bind listeners
        binding.buttonChangePicture.setOnClickListener(this)
    }

    private fun bindObservers() {
        // profile picture changed
        profileFormViewModel.pfpUriString.observe(requireActivity()) {
            val pfpUriString = profileFormViewModel.pfpUriString.value
            if (!pfpUriString.isNullOrBlank()) {
                binding.imageViewProfilePicture.setImageURI(pfpUriString.toUri())
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonChangePicture -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                photoPickerResultLauncher.launch(photoPickerIntent)
            }
        }
    }

    // on: photo picker activity result
    private var photoPickerResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data

                val user = userViewModel.loggedInUser.value
                if (uri != null) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)

                    // todo: maybe not save immediately? we don't know if the user will finish
                    //  unfortunately, this will require significant infrastructural changes
                    val physicalUriString = ExternalStorageSaver.saveBitmap(bitmap, ICON_WIDTH, ICON_HEIGHT) ?: return@registerForActivityResult

                    // apply partial user profile update with new picture
                    if (user != null) {
                        profileFormViewModel.updateFormPartial(PartialUserProfile(user.username).apply {
                            pictureURI = physicalUriString
                        })
                    }
                    // no user exists... (perhaps signing up?), just modify form data directly
                    else {
                        profileFormViewModel.pfpUriString.value = physicalUriString
                    }
                }
            }
        }

    /**
     * After signup, some fields (e.g. username) should not be able to be changed.
     */
    fun disableImmutableFields() {
        binding.textInputLayoutUsername.isEnabled = false
        binding.textInputLayoutUsername.helperText = "Cannot be changed after signup"
    }
}