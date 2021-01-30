package com.sneyder.biznearby.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.TypeUser
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.explore.ExploreFragment
import com.sneyder.biznearby.ui.login.LogInActivity
import com.sneyder.biznearby.ui.moderators.ModeratorsActivity
import com.sneyder.biznearby.ui.my_businesses.MyBusinessesActivity
import com.sneyder.biznearby.ui.reports.ReportsActivity
import com.sneyder.biznearby.utils.base.DaggerActivity
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.px
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class HomeActivity : DaggerActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val headerView by lazy { navView.getHeaderView(0) }
    private val profileImageView by lazy { headerView.findViewById<ImageView>(R.id.profileImageView) }
    private val fullnameTextView by lazy { headerView.findViewById<TextView>(R.id.fullnameTextView) }
    private val typeUserTextView by lazy { headerView.findViewById<TextView>(R.id.typeUserTextView) }
    private val emailTextView by lazy { headerView.findViewById<TextView>(R.id.emailTextView) }
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    companion object {

        fun navMenuItemsVisibleByUser(user: UserProfile?): List<Int> {
            debug("navMenuItemsVisibleByUser $user")
            if (user == null) return listOf()
            return when (user.getTypeUser()) {
                TypeUser.ADMIN -> {
                    listOf(
                        R.id.nav_add_business,
                        R.id.nav_my_businesses,
                        R.id.nav_reports,
                        R.id.nav_moderators,
                    )
                }
                TypeUser.MODERATOR -> {
                    listOf(
                        R.id.nav_add_business,
                        R.id.nav_my_businesses,
                        R.id.nav_reports
                    )
                }
                TypeUser.NORMAL -> {
                    listOf(
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

    private val exploreFragment: ExploreFragment? by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? ExploreFragment
    }

    private var isLoggedIn: Boolean = false
        set(value) {
            field = value
            if (value) {
                headerView.visibility = View.VISIBLE
                val params: FrameLayout.LayoutParams =
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 52.px)
                params.gravity = Gravity.BOTTOM
                params.setMargins(0, 0, 0, 0)
                logInButton.text = "Cerrar sesión"
            } else {
                headerView.visibility = View.GONE
                val params: FrameLayout.LayoutParams =
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 52.px)
                params.gravity = Gravity.TOP
                params.setMargins(params.leftMargin, 28.px, params.rightMargin, params.bottomMargin)
                logInButton.layoutParams = params
                logInButton.text = "Iniciar sesión"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        logInButton?.setOnClickListener {
            if (isLoggedIn) {
                viewModel.logOut()
            } else {
                startActivity(LogInActivity.starterIntent(this))
            }
        }
        queryEditText?.addTextChangedListener {
            debug("text changed $it")
            exploreFragment?.onQueryChange(it.toString())
        }
        observeUserProfile()
        observeLogOutResponse()
    }

    override fun onResume() {
        super.onResume()
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
                    isLoggedIn = false
//                    profileImageView.visibility = View.GONE
//                    fullnameTextView.visibility = View.GONE
//                    emailTextView.visibility = View.GONE
//                    typeUserTextView.visibility = View.GONE
                    setUpNavView(navMenuItemsVisibleByUser(userProfile))
                }
                it.isLoading -> {

                }
                else -> {
                    isLoggedIn = true
                    typeUserTextView.visibility =
                        if (TypeUser.valueOf(userProfile.typeUser.toUpperCase(Locale.ROOT)) != TypeUser.NORMAL) View.VISIBLE else View.GONE
                    fullnameTextView.text = userProfile.fullname
                    emailTextView.text = userProfile.email
                    typeUserTextView.text = userProfile.typeUser
                    Glide.with(this).load(userProfile.thumbnailUrl).centerCrop()
                        .placeholder(R.drawable.person_placeholder)
                        .into(profileImageView)
                    setUpNavView(navMenuItemsVisibleByUser(userProfile))
                }
            }
        }
    }

    private fun setUpNavView(menuItemsVisible: List<Int>) {
        ActionBarDrawerToggle(
            this@HomeActivity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }
        navView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.nav_my_businesses -> startActivity(MyBusinessesActivity.starterIntent(this))
                R.id.nav_moderators -> startActivity(ModeratorsActivity.starterIntent(this))
                R.id.nav_add_business -> startActivity(AddBusinessActivity.starterIntent(this))
                R.id.nav_reports -> startActivity(ReportsActivity.starterIntent(this))
            }
            true
        }
        navView.menu.forEach {
            it.isVisible = menuItemsVisible.contains(it.itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        val item = menu.findItem(R.id.range_spinner)
        val spinner = item.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.range_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                exploreFragment?.range = when (position) {
                    0 -> 200
                    1 -> 500
                    2 -> 1000
                    else -> 2000
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}