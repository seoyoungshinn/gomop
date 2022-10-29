package com.example.gomop.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gomop.R

class SearchFragment : Fragment() {
    override fun onCreateView(infoater: LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        return view
    }
}