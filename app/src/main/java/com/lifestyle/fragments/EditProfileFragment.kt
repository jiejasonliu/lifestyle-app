package com.lifestyle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lifestyle.R
import com.lifestyle.interfaces.IUserProfile

class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // use view.findViewById here as needed to grab elements from this fragment
    }

    /**
     * @return an unvalidated instance of IUserProfile
     */
    /*fun aggregateToUserProfile(): IUserProfile {
        TODO("Not implemented")
    }*/


}