package com.example.android3.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.android3.R
import com.example.android3.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        var isMaterial3 = sharedPref.getBoolean(R.string.theme_key.toString(), false)
        if (isMaterial3) {
            setTheme(R.style.Theme_MaterialYou)
        } else {
            setTheme(R.style.Theme_Material2)
        }
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}