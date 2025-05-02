package com.example.maintrack

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class OnboardingFragment5 : Fragment(R.layout.fragment_onboarding5) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finishButton = view.findViewById<Button>(R.id.finish_button)
        finishButton.setOnClickListener {
            // Marcar que o onboarding foi concluído
            val sharedPref = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE)
            sharedPref.edit().putBoolean("onboarding_done", true).apply()


            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }
}