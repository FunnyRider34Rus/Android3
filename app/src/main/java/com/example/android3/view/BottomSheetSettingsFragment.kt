package com.example.android3.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android3.R
import com.example.android3.databinding.BottonSheetSettingsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSettingsFragment : BottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = BottomSheetSettingsFragment()
    }

    private var _binding: BottonSheetSettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private var isMaterial3 = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottonSheetSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        isMaterial3 = sharedPref?.getBoolean(R.string.theme_key.toString(), false) ?: false
        binding.switchSettings.isChecked = isMaterial3
        binding.switchSettings.setOnCheckedChangeListener { compoundButton, b ->
            isMaterial3 = binding.switchSettings.isChecked

            sharedPref?.edit()?.putBoolean(R.string.theme_key.toString(), isMaterial3)?.apply()
            activity?.recreate()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}