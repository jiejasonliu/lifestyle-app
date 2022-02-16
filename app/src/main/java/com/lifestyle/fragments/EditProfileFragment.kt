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
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toUri
import com.lifestyle.helpers.ExternalStorageSaver
import java.io.File
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

    private var currentPictureUriString: String? = null

    companion object {
        const val ICON_WIDTH: Int = 128
        const val ICON_HEIGHT: Int = 128
    }

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
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)

                    // todo: maybe not save immediately? we don't know if the user will finish
                    //  unfortunately, this will require significant infrastructural changes
                    val physicalUriString =
                        ExternalStorageSaver.saveBitmap(bitmap, ICON_WIDTH, ICON_HEIGHT)
                    if (physicalUriString != null) {
                        currentPictureUriString = physicalUriString
                        profileImageView.setImageURI(physicalUriString.toUri())
                    }
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
        // note: field lengths are limited via values in @strings/... (strings.xml)
        // the purpose here is to validate things that aren't possible via XML

        // username pass
        val textUsername = getText(textLayoutUsername)
        if (textUsername == null)
            return withErr(textLayoutUsername.hint, "Must not be empty")
        else if (textUsername.length < 6)
            return withErr(textLayoutUsername.hint, "Too short")

        // full name pass
        val textFullName = getText(textLayoutFullName)
        if (textFullName == null)
            return withErr(textLayoutFullName.hint, "Must not be empty")
        else if (textFullName.length < 6)
            return withErr(textLayoutFullName.hint, "Too short")

        // weight pass
        val textWeight = getText(textLayoutWeight)
        if (textWeight != null) {
            if (textWeight.toInt() < 1)
                return withErr(textLayoutWeight.hint, "Must be greater than 1")
        }

        // height pass
        // ft.
        val textHeightFt = getText(textLayoutHeightFt)
        if (textHeightFt != null) {    // validate only if it has contents
            if (textHeightFt.toInt() < 0 || textHeightFt.toInt() > 11)
                return withErr(textLayoutHeightFt.hint, "Must be between 0-11")
        }

        // in.
        val textHeightIn = getText(textLayoutHeightIn)
        if (textHeightIn != null) {    // validate only if it has contents
            if (textHeightIn.toInt() < 0 || textHeightIn.toInt() > 11)
                return withErr(textLayoutHeightIn.hint, "Must be between 0-11")
        }

        // if one is filled, the other should be also filled
        if ((textHeightFt != null) xor (textHeightIn != null)) {
            return if (textHeightFt == null)
                withErr(textLayoutHeightFt.hint, "Don't leave feet blank if inches is filled")
            else
                withErr(textLayoutHeightIn.hint, "Don't leave inches blank if feet is filled")
        }

        if (context == null)
            return withErr("Lifestyle", "Something went wrong")

        // create user in model storage
        val username: String = getText(textLayoutUsername)!! // username must be filled
        val user = StoredUser(requireContext(), username)
        writeToStoredUser(user)

        return EditProfileResult(
            success = true,
            userProfile = user
        )
    }

    /**
     * Fill fragment text fields with applicable data from a StoredUser instance.
     */
    fun fillProfileFields(user: StoredUser) {
        fun EditText.setIfExists(str: String?) {
            if (!str.isNullOrBlank())
                this.setText(str)
        }
        fun EditText.setIfExists(int: Int?) {
            if (int != null)
                this.setText(int.toString())
        }

        if (user.pictureURI != null) {
            currentPictureUriString = user.pictureURI
            profileImageView.setImageURI(user.pictureURI!!.toUri())
        }

        textLayoutUsername.editText?.setIfExists(user.username)
        textLayoutFullName.editText?.setIfExists(user.fullName)
        textLayoutAge.editText?.setIfExists(user.age)
        textLayoutCity.editText?.setIfExists(user.city)
        textLayoutState.editText?.setIfExists(user.state)
        textLayoutCountry.editText?.setIfExists(user.country)
        textLayoutSex.editText?.setIfExists(user.sex)
        textLayoutWeight.editText?.setIfExists(user.weight)

        if (user.height != null) {
            val feet = user.height?.div(12)
            val inches = user.height?.mod(12)
            textLayoutHeightFt.editText?.setIfExists(feet)
            textLayoutHeightIn.editText?.setIfExists(inches)
        }
    }

    /**
     * After signup, some fields (e.g. username) should not be able to be changed.
     */
    fun disableImmutableFields() {
        textLayoutUsername.isEnabled = false
        textLayoutUsername.helperText = "Cannot be changed after signup"
    }


    /**
     * @return the text; or null if the text is empty/blank
     */
    private fun getText(layout: TextInputLayout?): String? {
        val result = layout?.editText?.text?.toString()?.trim()
        if (result != null && (!result.isNullOrBlank()))
            return result

        return null
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
        val fullName: String = getText(textLayoutFullName)!!    // full name must be filled
        val age: Int? = getText(textLayoutAge)?.toInt()
        val city: String? = getText(textLayoutCity)
        val state: String? = getText(textLayoutState)
        val country: String? = getText(textLayoutCountry)
        val sex: String? = getText(textLayoutSex)
        val weight: Int? = getText(textLayoutWeight)?.toInt()

        val heightFt: Int? = getText(textLayoutHeightFt)?.toInt()
        val heightIn: Int? = getText(textLayoutHeightIn)?.toInt()
        var height: Int? = null
        if (heightFt != null && heightIn != null) {
            height = (12 * heightFt) + heightIn
        }

        // setters are a no-op if field is null
        user.fullName = fullName
        user.age = age
        user.city = city
        user.state = state
        user.country = country
        user.sex = sex
        user.weight = weight
        user.height = height
        user.pictureURI = currentPictureUriString
    }
}