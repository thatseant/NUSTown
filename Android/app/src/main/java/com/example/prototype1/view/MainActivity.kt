package com.example.prototype1.view;

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.prototype1.R
import com.example.prototype1.databinding.ActivityMainBinding
import com.example.prototype1.viewmodel.TitleFragmentViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private lateinit var mViewModel: TitleFragmentViewModel
    private val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(TitleFragmentViewModel::class.java)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (shouldStartSignIn()) {
            startSignIn()
            return
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
            R.id.menu_add_items -> {
//                onAddItemsClicked()
                true
            }
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



