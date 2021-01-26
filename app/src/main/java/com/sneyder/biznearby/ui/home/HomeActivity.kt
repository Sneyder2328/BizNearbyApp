package com.sneyder.biznearby.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.TypeUser
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.ui.login.LogInActivity
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

class HomeActivity : DaggerActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val headerView by lazy { navView.getHeaderView(0) }
    private val logInButton by lazy { headerView.findViewById<Button>(R.id.logInButton) }
    private val profileImageView by lazy { headerView.findViewById<ImageView>(R.id.profileImageView) }
    private val fullnameTextView by lazy { headerView.findViewById<TextView>(R.id.fullnameTextView) }
    private val typeUserTextView by lazy { headerView.findViewById<TextView>(R.id.typeUserTextView) }
    private val emailTextView by lazy { headerView.findViewById<TextView>(R.id.emailTextView) }
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    companion object {

        fun navMenuItemsVisibleByUser(user: UserProfile?): List<Int> {
            debug("navMenuItemsVisibleByUser $user")
            if (user == null) return listOf(R.id.nav_explore)
            return when (user.getTypeUser()) {
                TypeUser.ADMIN -> {
                    listOf(
                        R.id.nav_explore,
                        R.id.nav_add_business,
                        R.id.nav_my_businesses,
                        R.id.nav_reports,
                        R.id.nav_moderators,
                    )
                }
                TypeUser.MODERATOR -> {
                    listOf(
                        R.id.nav_explore,
                        R.id.nav_add_business,
                        R.id.nav_my_businesses,
                        R.id.nav_reports
                    )
                }
                TypeUser.NORMAL -> {
                    listOf(
                        R.id.nav_explore,
                        R.id.nav_add_business,
                        R.id.nav_my_businesses,
                    )
                }
            }
        }

        fun starterIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        logInButton.setOnClickListener { startActivity(LogInActivity.starterIntent(this)) }

        observeUserProfile()
        observeLogOutResponse()
        viewModel.loadCurrentUserProfile()
    }

    private fun observeLogOutResponse() {
        viewModel.logOutResponse.observe(this) {

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        debug("HomeActivity onNewIntent")
        viewModel.loadCurrentUserProfile()
    }

    private fun observeUserProfile() {
        viewModel.userProfile.observe(this) {
            val userProfile = it?.success
            debug("observeUserProfile $userProfile")
            when {
                it == null || userProfile == null -> {
                    logInButton.visibility = View.VISIBLE
                    profileImageView.visibility = View.GONE
                    fullnameTextView.visibility = View.GONE
                    emailTextView.visibility = View.GONE
                    typeUserTextView.visibility = View.GONE
                    invalidateOptionsMenu()
                    setUpNavView(navMenuItemsVisibleByUser(userProfile))
                }
                it.isLoading -> {

                }
                else -> {
                    logInButton.visibility = View.GONE
                    profileImageView.visibility = View.VISIBLE
                    fullnameTextView.visibility = View.VISIBLE
                    emailTextView.visibility = View.VISIBLE
                    typeUserTextView.visibility =
                        if (TypeUser.valueOf(userProfile.typeUser.toUpperCase(Locale.ROOT)) != TypeUser.NORMAL) View.VISIBLE else View.GONE
                    fullnameTextView.text = userProfile.fullname
                    emailTextView.text = userProfile.email
                    typeUserTextView.text = userProfile.typeUser
                    Glide.with(this).load(userProfile.thumbnailUrl).centerCrop()
                        .placeholder(R.drawable.person_placeholder)
                        .into(profileImageView)
                    invalidateOptionsMenu()
                    setUpNavView(navMenuItemsVisibleByUser(userProfile))
                }
            }
        }
    }

    private fun setUpNavView(menuItemsVisible: List<Int>) {
        debug("setUpNavView $menuItemsVisible")
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            menuItemsVisible.toSet(), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        ActionBarDrawerToggle(
            this@HomeActivity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }

        navView.menu.forEach {
            it.isVisible = menuItemsVisible.contains(it.itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_log_out)?.isVisible = viewModel.userProfile.value != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_log_out -> logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOut() {
        viewModel.logOut()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}