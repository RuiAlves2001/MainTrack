package com.ipvc.maintrack

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2


class OnboardingFragment1 : Fragment(R.layout.fragment_onboarding1) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextButton = view.findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            // Avançar para o próximo fragmento
            (activity as? MainActivity)?.findViewById<ViewPager2>(R.id.viewPager)?.currentItem = 1
        }
    }
}