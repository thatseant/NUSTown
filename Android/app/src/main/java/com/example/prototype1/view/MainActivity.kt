package com.example.prototype1.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.prototype1.R
import com.example.prototype1.viewmodel.TitleFragmentViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private lateinit var mViewModel: TitleFragmentViewModel
    private val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(TitleFragmentViewModel::class.java)
//        @Suppress("UNUSED_VARIABLE")
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.myNavHostFragment)
        findViewById<BottomNavigationView>(R.id.bottom_nav)
                .setupWithNavController(navController)
//        NavigationUI.setupWithNavController(bottom_nav, navController)
    }


    override fun onStart() {
        super.onStart()

        if (shouldStartSignIn()) {
            startSignIn()
            return
        } else {
            val mModel = ViewModelProvider(this).get(TitleFragmentViewModel::class.java)

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                mModel.setUser(user.email)
            }
        }
    }

    private fun shouldStartSignIn(): Boolean {
        return (!mViewModel.isSigningIn && (FirebaseAuth.getInstance().currentUser == null))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(this)
                startSignIn()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startSignIn() {
        startActivityForResult( //Launches login Activity
                AuthUI.getInstance() //Firebase UI provides own UI for login and simplifies process
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers) //Only email thus far
                        .build(),
                RC_SIGN_IN)
        mViewModel.isSigningIn = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //on completion of login
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            mViewModel.isSigningIn = false

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn()
            }
        }
    }
}



