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

class MainFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<FrameLayout>

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
        viewModel.sendServerRequest(getDate(0))

        binding.chips.setOnCheckedChangeListener { group, checkedId ->
            binding.chips.findViewById<Chip>(checkedId)?.let {
                when(checkedId) {
                    1 -> viewModel.sendServerRequest(getDate(2))
                    2 -> viewModel.sendServerRequest(getDate(1))
                    3 -> viewModel.sendServerRequest(getDate(0))
                }
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.included.bottomSheetContainer)

        //обработка поля ввода текстового поиска
        binding.search.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${binding.input.text.toString()}")
            })
        }
    }

    private fun renderData(apodState: APODState) {
        when (apodState) {
            is APODState.Error -> {
                Snackbar.make(binding.root, getString(R.string.error_loading),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.error_repeat)
                ) {  }.show()
            }
            is APODState.Loading -> {
                binding.progressBar.isVisible = true
                binding.fragmentMainView.isVisible = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.included.bottomSheetContainer
            }
            is APODState.Success -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                binding.apply {
                    progressBar.isVisible = false
                    fragmentMainView.isVisible = true
                    pictOfTheDay.load(apodState.serverResponseData.hdurl)
                    included.bottomSheetDescriptionHeader.text = apodState.serverResponseData.title
                    included.bottomSheetDescriptionBody.text = apodState.serverResponseData.explanation
                }

            }
        }

    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(view as Toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_pict -> Toast.makeText(requireContext(),"Favorite",Toast.LENGTH_SHORT).show()
            R.id.menu_settings -> Toast.makeText(requireContext(),"Settings",Toast.LENGTH_SHORT).show()
            android.R.id.home -> {
                BottomSheetNavFragment().show(requireActivity().supportFragmentManager,"")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(day: Long): LocalDate? {
        var date = LocalDate.now()
        date = date.minusDays(day)
        return date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}