package com.example.restaurantapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.restaurantapp.MainActivity
import com.example.restaurantapp.R

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.tvProfileMessage)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        textView.text = "프로필 기능은 추후 구현 예정입니다."

        btnLogout.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }
}