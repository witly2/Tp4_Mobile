package com.example.tp4

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.example.tp4.databinding.ConnexionInscriptionBinding
import com.example.tp4.R.id.nav_host_fragment_content
import com.example.tp4.R.id.nav_connexion
import com.example.tp4.R.id.nav_details
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ConnexionInscriptionActivity: AppCompatActivity() {
    private lateinit var binding: ConnexionInscriptionBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var mAuth: FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ConnexionInscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instance du service d'authentification
        mAuth = FirebaseAuth.getInstance()

        // Si token user déjà valide
        if (mAuth!!.currentUser != null) {
            Log.d("***TAG", "onCreate: " + mAuth!!.currentUser)
            //Rediriger vers l'accueil

            Toast.makeText(this, "Vous êtes déjà connecté.", Toast.LENGTH_SHORT).show()
            updateUi(mAuth!!.currentUser)
        }

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView2

        val navController = findNavController(nav_host_fragment_content)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                nav_connexion
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(nav_host_fragment_content)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateUi(user: FirebaseUser?) {
        // token retourné par Firebase : utile si on doit par la suite communiquer avec un backend tierce
        //binding.textView5.setText("Bienvenue "+user.getIdToken(true));
        //binding.textView5.setText("Bienvenue " + user!!.displayName)

        /*if (user.isEmailVerified == true) binding.textView6.setText("Courriel vérifié") else binding.textView6.setText(
            "Courriel non vérifié"
        )*/
        startActivity(Intent(this, MainActivity::class.java))
    }

  /*  override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().signOut()
    }
*/}


