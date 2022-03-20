package com.example.android3.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.android3.R
import com.example.android3.databinding.BottomSheetNavFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetNavFragment : BottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = BottomSheetNavFragment()
    }

    private var _binding: BottomSheetNavFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetNavFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.item1 -> Toast.makeText(requireContext(),"Screen1", Toast.LENGTH_SHORT).show()
                R.id.item2 -> Toast.makeText(requireContext(),"Screen2", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}