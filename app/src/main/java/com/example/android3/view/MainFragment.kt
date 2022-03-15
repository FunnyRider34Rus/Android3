package com.example.android3.view

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import coil.load
import com.example.android3.databinding.MainFragmentBinding
import com.example.android3.viewmodel.APODState
import com.example.android3.R
import com.example.android3.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomAppBar(binding.bottomAppbar)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        viewModel.sendServerRequest()

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
                ) { viewModel.sendServerRequest() }.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}