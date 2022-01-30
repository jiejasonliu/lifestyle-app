package com.lifestyle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputLayout
import com.lifestyle.R
import com.lifestyle.models.EditProfileResult

class EditProfileFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textLayoutUsername = view.findViewById(R.id.textInputLayoutUsername)
        textLayoutFullName = view.findViewById(R.id.textInputLayoutFullName)
        textLayoutAge = view.findViewById(R.id.textInputLayoutCity)
        textLayoutCity = view.findViewById(R.id.textInputLayoutCity)
        textLayoutState = view.findViewById(R.id.textInputLayoutState)
        textLayoutCountry = view.findViewById(R.id.textInputLayoutCountry)
        textLayoutSex = view.findViewById(R.id.textInputLayoutSex)
        textLayoutWeight = view.findViewById(R.id.textInputLayoutWeight)
        textLayoutHeightFt = view.findViewById(R.id.textInputLayoutHeightFt)
        textLayoutHeightIn = view.findViewById(R.id.textInputLayoutHeightIn)
    }

    /**
     * @return <T> aggregate fields into <T>.userProfile if <T>.success is true
     *          otherwise <T>.firstError will contain the first error found
     */
    fun aggregateFields(): EditProfileResult? {
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

        // todo: everything below ooga booga

        // weight pass
        if (getText(textLayoutWeight).isNullOrBlank())
            return withErr(textLayoutWeight.hint, "Must not be empty")

        // height pass
        if (getText(textLayoutHeightFt).isNullOrBlank())
            return withErr(textLayoutWeight.hint, "Must not be empty")

        if (getText(textLayoutHeightIn).isNullOrBlank())
            return withErr(textLayoutWeight.hint, "Must not be empty")

        return null
    }

    private fun getText(layout: TextInputLayout?): String {
        println(layout?.editText?.text?.toString()?.trim() ?: "")
        return layout?.editText?.text?.toString()?.trim() ?: ""
    }

    private fun withErr(atViewName: String, errorDetails: String): EditProfileResult {
        return EditProfileResult(
            success=true,
            firstError="$atViewName: $errorDetails",
            userProfile=null,
        )
    }

    private fun withErr(atViewName: CharSequence?, errorDetails: String): EditProfileResult {
        return EditProfileResult(
            success=true,
            firstError="$atViewName: $errorDetails",
            userProfile=null,
        )
    }


}