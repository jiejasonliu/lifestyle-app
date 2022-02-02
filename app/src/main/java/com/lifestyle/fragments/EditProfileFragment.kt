package com.lifestyle.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.lifestyle.R
import com.lifestyle.interfaces.IUserProfile
import com.lifestyle.models.EditProfileResult
import com.lifestyle.models.StoredUser
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import java.net.URI


class EditProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var changePictureButton: Button
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var textLayoutUsername: TextInputLayout
    private lateinit var textLayoutFullName: TextInputLayout
    private lateinit var textLayoutAge: TextInputLayout
    private lateinit var textLayoutCity: TextInputLayout
    private lateinit var textLayoutState: TextInputLayout
    private lateinit var textLayoutCountry: TextInputLayout
    private lateinit var textLayoutSex: TextInputLayout
    private lateinit var textLayoutWeight: TextInputLayout
    private lateinit var textLayoutHeightFt: TextInputLayout
    private lateinit var textLayoutHeightIn: TextInputLayout

    private var pictureURI: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changePictureButton = view.findViewById(R.id.buttonChangePicture)
        profileImageView = view.findViewById(R.id.imageViewProfilePicture)
        textLayoutUsername = view.findViewById(R.id.textInputLayoutUsername)
        textLayoutFullName = view.findViewById(R.id.textInputLayoutFullName)
        textLayoutAge = view.findViewById(R.id.textInputLayoutAge)
        textLayoutCity = view.findViewById(R.id.textInputLayoutCity)
        textLayoutState = view.findViewById(R.id.textInputLayoutState)
        textLayoutCountry = view.findViewById(R.id.textInputLayoutCountry)
        textLayoutSex = view.findViewById(R.id.textInputLayoutSex)
        textLayoutWeight = view.findViewById(R.id.textInputLayoutWeight)
        textLayoutHeightFt = view.findViewById(R.id.textInputLayoutHeightFt)
        textLayoutHeightIn = view.findViewById(R.id.textInputLayoutHeightIn)

        // save URI for when user submits the form
        changePictureButton.setOnClickListener(this)
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

                // fill in pictureURI field and update UI
                if (uri != null) {
                    pictureURI = uri.toString()
                    profileImageView.setImageURI(uri)
                }
            }
        }

    /**
     * If all the fields are validated, write user profile into SharedPreferences.
     *
     * @return <T> aggregate fields into <T>.userProfile if <T>.success is true
     *          otherwise <T>.firstError will contain the first error found
     */
    fun aggregateFieldsAndWrite(): EditProfileResult? {
        // note: field lengths are limited via @strings/... (strings.xml)
        // the purpose here is to validate things that aren't possible via XML

        // username pass
        if (getText(textLayoutUsername).length < 6)
            return withErr(textLayoutUsername.hint, "Too short")

        // full name pass
        if (getText(textLayoutFullName).length < 6)
            return withErr(textLayoutFullName.hint, "Too short")

        // age pass
        if (getText(textLayoutAge).isNullOrBlank())
            return withErr(textLayoutAge.hint, "Invalid number")

        // city pass
        if (getText(textLayoutCity).isNullOrBlank())
            return withErr(textLayoutCity.hint, "Must not be empty")

        // state pass
        if (getText(textLayoutState).isNullOrBlank())
            return withErr(textLayoutState.hint, "Must not be empty")

        // country pass
        if (getText(textLayoutCountry).isNullOrBlank())
            return withErr(textLayoutCountry.hint, "Must not be empty")

        // sex pass
        if (getText(textLayoutSex).isNullOrBlank())
            return withErr(textLayoutSex.hint, "Must not be empty")

        // weight pass
        if (getText(textLayoutWeight).isNullOrBlank())
            return withErr(textLayoutWeight.hint, "Must not be empty")
        else if (getText(textLayoutWeight).toInt() < 1)
            return withErr(textLayoutWeight.hint, "Must be greater than 1")

        // height pass
        // ft.
        if (getText(textLayoutHeightFt).isNullOrBlank())
            return withErr(textLayoutHeightFt.hint, "Must not be empty")
        else if (getText(textLayoutHeightFt).toInt() < 0 || getText(textLayoutHeightFt).toInt() > 11)
            return withErr(textLayoutHeightFt.hint, "Must be between 0-11")

        // in.
        if (getText(textLayoutHeightIn).isNullOrBlank())
            return withErr(textLayoutHeightIn.hint, "Must not be empty")
        else if (getText(textLayoutHeightIn).toInt() < 0 || getText(textLayoutHeightIn).toInt() > 11)
            return withErr(textLayoutHeightIn.hint, "Must be between 0-11")

        if (context == null)
            return withErr("Lifestyle", "Something went wrong")

        // create user in model storage
        val username: String = getText(textLayoutUsername)
        val user = StoredUser(requireContext(), username)
        writeToStoredUser(user)

        return EditProfileResult(
            success = true,
            userProfile = user
        )
    }

    private fun getText(layout: TextInputLayout?): String {
        return layout?.editText?.text?.toString()?.trim() ?: ""
    }

    private fun withErr(atViewName: String, errorDetails: String): EditProfileResult {
        return EditProfileResult(
            success = false,
            firstError = "$atViewName: $errorDetails",
            userProfile = null,
        )
    }

    private fun withErr(atViewName: CharSequence?, errorDetails: String): EditProfileResult {
        return EditProfileResult(
            success = false,
            firstError = "$atViewName: $errorDetails",
            userProfile = null,
        )
    }

    private fun writeToStoredUser(user: StoredUser) {
        val username: String = getText(textLayoutUsername)
        val fullName: String = getText(textLayoutFullName)
        val age: Int = getText(textLayoutAge).toInt()
        val city: String = getText(textLayoutCity)
        val state: String = getText(textLayoutState)
        val country: String = getText(textLayoutCountry)
        val sex: String = getText(textLayoutSex)
        val weight: Int = getText(textLayoutWeight).toInt()
        val height: Int =
            (12 * getText(textLayoutHeightFt).toInt()) + getText(textLayoutHeightIn).toInt()

        user.fullName = fullName
        user.age = age
        user.city = city
        user.state = state
        user.country = country
        user.sex = sex
        user.weight = weight
        user.height = height

        user.pictureURI = pictureURI    // null <> default image
    }
}