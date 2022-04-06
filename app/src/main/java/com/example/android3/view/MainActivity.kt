package com.example.android3.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import com.example.android3.R
import com.example.android3.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        //получение и установка темы
        val isMaterial3 = sharedPref.getBoolean(R.string.theme_key.toString(), false)
        if (isMaterial3) {
            setTheme(R.style.Theme_MaterialYou)
        } else {
            setTheme(R.style.Theme_Material2)
        }

        setContentView(binding.root)
        if (savedInstanceState == null) {
            refreshFragment()
        }

        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    sharedPref?.edit()?.putLong(R.string.nav_key.toString(), 0L)?.apply()
                    refreshFragment()
                    true
                }
                R.id.yesterday -> {
                    sharedPref?.edit()?.putLong(R.string.nav_key.toString(), 1L)?.apply()
                    refreshFragment()
                    true
                }
                R.id.before -> {
                    sharedPref?.edit()?.putLong(R.string.nav_key.toString(), 2L)?.apply()
                    refreshFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun refreshFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commitNow()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}