package com.example.android3.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import coil.load
import com.example.android3.databinding.MainFragmentBinding
import com.example.android3.viewmodel.APODState
import com.example.android3.R
import com.example.android3.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate

private const val CURRENT_DAY = "currentday"
private var day: Long? = 0

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentDay: LocalDate? = getDate(0)     //установка текущей даты

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            day = savedInstanceState.getLong(CURRENT_DAY);
            currentDay = getDate(day!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomAppBar(binding.bottomAppbar)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        binding.chips.check(R.id.chip_today)
        updateScreen(currentDay)        //отображаем экран по умолчанию

        //обратока чипов и перерисовка экранов
        binding.chips.setOnCheckedChangeListener { group, checkedId ->
            binding.chips.findViewById<Chip>(checkedId)?.let {
                when (checkedId) {
                    R.id.chip_before_yesterday -> {
                        day = 2
                        currentDay = getDate(day!!)
                        updateScreen(currentDay)
                    }
                    R.id.chip_yesterday -> {
                        day = 1
                        currentDay = getDate(day!!)
                        updateScreen(currentDay)
                    }
                    R.id.chip_today -> {
                        day = 0
                        currentDay = getDate(day!!)
                        updateScreen(currentDay)
                    }
                }
            }
        }

        //находим behavior BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.included.bottomSheetContainer)

        //обработка поля ввода текстового поиска
        binding.search.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${binding.input.text.toString()}")
            })
        }
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
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.apply {
                    progressBar.isVisible = true
                    fragmentMainView.isVisible = false
                    included.bottomSheetContainer
                }
            }
            is APODState.Success -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                binding.apply {
                    progressBar.isVisible = false
                    fragmentMainView.isVisible = true
                    pictOfTheDay.load(apodState.serverResponseData.hdurl)
                    included.bottomSheetDescriptionHeader.text = apodState.serverResponseData.title
                    included.bottomSheetDescriptionBody.text =
                        apodState.serverResponseData.explanation
                }

            }
        }
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(view as Toolbar)
        setHasOptionsMenu(true)
    }

    //отображаем AppBar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom, menu)
    }

    //обработка AppBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pict -> Toast.makeText(requireContext(), "Favorite", Toast.LENGTH_SHORT)
                .show()
            R.id.menu_settings -> Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT)
                .show()
            android.R.id.home -> {
                BottomSheetNavFragment().show(requireActivity().supportFragmentManager, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //установка нужной даты
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(day: Long): LocalDate {
        var date = LocalDate.now()
        date = date.minusDays(day)
        return date
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        day?.let { outState.putLong(CURRENT_DAY, it) }
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