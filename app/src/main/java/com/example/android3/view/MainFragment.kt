package com.example.android3.view

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import coil.load
import com.example.android3.databinding.MainFragmentBinding
import com.example.android3.viewmodel.APODState
import com.example.android3.R
import com.example.android3.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate

private var day: Long? = 0

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        day = sharedPref.getLong(R.string.nav_key.toString(), 0)
        updateScreen(getDate(day!!))        //отображаем экран по умолчанию
    }

    //отображение актуальной информации
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateScreen(currentDay: LocalDate?) {
        viewModel.sendServerRequest(currentDay)
    }

    private fun renderData(apodState: APODState) {
        when (apodState) {
            is APODState.Error -> {
                Snackbar.make(
                    binding.root, getString(R.string.error_loading),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    getString(R.string.error_repeat)
                ) { }.show()
            }
            is APODState.Loading -> {
                binding.apply {
                    progressBar.isVisible = true
                    fragmentMainView.isVisible = false
                }
            }
            is APODState.Success -> {
                binding.apply {
                    progressBar.isVisible = false
                    fragmentMainView.isVisible = true
                    pictOfTheDay.load(apodState.serverResponseData.hdurl)
                    bottomSheetDescriptionHeader.title = apodState.serverResponseData.title
                    bottomSheetDescriptionBody.text =
                        apodState.serverResponseData.explanation
                }

            }
        }
    }

    //установка нужной даты
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(day: Long): LocalDate {
        var date = LocalDate.now()
        date = date.minusDays(day)
        return date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}