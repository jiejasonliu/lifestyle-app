package com.lifestyle.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import com.lifestyle.*
import com.lifestyle.viewmodels.UserViewModel

class SideBarFragment : Fragment(), View.OnClickListener {

    lateinit var cardViewProfileTablet: CardView
    lateinit var cardViewBmiTablet: CardView
    lateinit var cardViewHikingTablet: CardView
    lateinit var cardViewWeatherTablet: CardView
    lateinit var cardViewSettingsTablet: CardView
    lateinit var cardViewHomeTablet: CardView

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_side_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardViewProfileTablet = view.findViewById(R.id.cardViewProfileTablet)
        cardViewBmiTablet = view.findViewById(R.id.cardViewBmiTablet)
        cardViewHikingTablet = view.findViewById(R.id.cardViewHikingTablet)
        cardViewWeatherTablet = view.findViewById(R.id.cardViewWeatherTablet)
        cardViewSettingsTablet = view.findViewById(R.id.cardViewSettingsTablet)
        cardViewHomeTablet = view.findViewById(R.id.cardViewHomeTablet)

        // bind listeners
        cardViewProfileTablet.setOnClickListener(this)
        cardViewBmiTablet.setOnClickListener(this)
        cardViewHikingTablet.setOnClickListener(this)
        cardViewWeatherTablet.setOnClickListener(this)
        cardViewSettingsTablet.setOnClickListener(this)
        cardViewHomeTablet.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cardViewProfileTablet -> {
                if (!userViewModel.isLoggedIn()) {
                    Toast.makeText(requireActivity(), "User must be logged in", Toast.LENGTH_SHORT).show()
                    return
                }

                startActivity(Intent(requireActivity(), ProfileActivity::class.java))
            }

            R.id.cardViewBmiTablet -> {
                startActivity(Intent(requireActivity(), FitnessActivity::class.java))
            }

            R.id.cardViewHikingTablet -> {
                startActivity(Intent(requireActivity(), HikingActivity::class.java))
            }

            R.id.cardViewWeatherTablet -> {
                if (!userViewModel.isLoggedIn()) {
                    Toast.makeText(requireActivity(), "User must be logged in", Toast.LENGTH_SHORT).show()
                    return
                }
                startActivity(Intent(requireActivity(), WeatherActivity::class.java))
            }

            R.id.cardViewSettingsTablet -> {
                Toast.makeText(requireActivity(), "Settings Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewHomeTablet -> {
                Toast.makeText(requireActivity(), "Home Clicked", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }
    }

}