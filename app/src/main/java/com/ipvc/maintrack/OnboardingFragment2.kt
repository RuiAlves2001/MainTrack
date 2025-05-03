package com.ipvc.maintrack

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2


class OnboardingFragment2 : Fragment(R.layout.fragment_onboarding2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextButton = view.findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            val viewPager = (activity as? MainActivity)?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.currentItem = (viewPager?.currentItem ?: 0) + 1
        }
    }
}