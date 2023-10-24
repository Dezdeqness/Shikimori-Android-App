package com.dezdeqness.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dezdeqness.R
import com.dezdeqness.core.BackFragmentListener
import com.dezdeqness.databinding.ActivityMainBinding
import com.dezdeqness.extensions.setupWithNavController
import com.dezdeqness.getComponent
import com.dezdeqness.ui.ShikimoriSnackbar
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel>(
        factoryProducer = {
            viewModelFactory
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application
            .getComponent()
            .mainComponent()
            .create()
            .inject(this)

        lifecycleScope.launch {
            val status = application.getComponent().settingsRepository().getNightThemeStatus()
            if (status) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        setupObservers()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.firstOrNull { it.isAdded && it.isVisible }
            ?: return

        val lastFragment =
            fragment.childFragmentManager.fragments.filterIsInstance<BackFragmentListener>()
                .lastOrNull()
        val isBackOk = lastFragment?.isBackNeed() ?: true

        if (isBackOk) {
            super.onBackPressed()
        } else {
            lastFragment?.onBackPressed()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val bottomVav = binding.navigation

        val navGraphIds = listOf(
            R.navigation.personal_host_nav_graph,
            R.navigation.calendar_nav_graph,
            R.navigation.search_nav_graph,
            R.navigation.profile_host_nav_graph,
        )

        bottomVav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.container,
            intent = intent
        )

    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.messageState.collect { event ->
                    ShikimoriSnackbar
                        .showSnackbarShort(
                            view = binding.root,
                            anchorView = binding.navigation,
                            event = event,
                        )
                }
            }
        }
    }

}
