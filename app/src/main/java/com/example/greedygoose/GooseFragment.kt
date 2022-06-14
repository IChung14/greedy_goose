package com.example.greedygoose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greedygoose.databinding.FragmentGooseBinding

class GooseFragment : Fragment() {

    private lateinit var binding: FragmentGooseBinding

    companion object {
//        private const val ARG_ID = "id"
//        private const val ARG_FOREGROUND = "foreground"
//        private const val ARG_PREPOPULATE_TEXT = "prepopulate_text"

        fun newInstance() =
            GooseFragment().apply {
                arguments = Bundle().apply {
//                    putLong(ARG_ID, id)
//                    putBoolean(ARG_FOREGROUND, foreground)
//                    putString(ARG_PREPOPULATE_TEXT, prepopulateText)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGooseBinding.inflate(inflater, container, false)
        return binding.root
    }
}