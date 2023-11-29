package com.example.tp4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.example.tp4.databinding.ConnexionBinding
import com.example.tp4.ui.home.HomeFragment
import com.example.tp4.ui.inscription.InscriptionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class ConnexionInscriptionActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ConnexionBinding
    private lateinit var textViewEmail:TextView
    private lateinit var textViewPassword:TextView
    private lateinit var textError:TextView
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ConnexionBinding.inflate(layoutInflater)
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

        textViewEmail=binding.editTextTextEmail
        textViewPassword=binding.editTextTextPassword
        textError=binding.textError
        binding.textView2.setOnClickListener(this)

        // Connexion
        binding.buttonConnexion.setOnClickListener(this)

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.textView2 -> {
                startActivity(Intent(this@ConnexionInscriptionActivity, InscriptionFragment::class.java))
            }
            R.id.button_connexion -> {
                signIn(textViewEmail.getText().toString().trim(),
                    textViewPassword.getText().toString().trim())
            }
        }
    }

    private fun signIn(courriel: String, mdp: String) {

        mAuth!!.signInWithEmailAndPassword(courriel, mdp)
            .addOnCompleteListener(
                this
            ) { task ->
                Toast.makeText(
                    this, "Connexion en cours ...",
                    Toast.LENGTH_SHORT,
                ).show()
                if (task.isSuccessful) {
                    // Firebase User est un user connecté
                    val user = mAuth!!.currentUser
                    Toast.makeText(
                        this, "Connexion réussie",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUi(user)

                }/* else {
                    Log.w(
                        SignInActivity.Companion.TAG,
                        "signInWithEmail:failure",
                        task.exception
                    )
                }*/
            } // Ce callback nous permet d'avoir accès aux exceptions
            // Important, car permet l'identification exacte de l'erreur à la connexion
            .addOnFailureListener { e ->
                textError.setPadding(20)
                textError.backgroundTintList= ContextCompat.getColorStateList(this, R.color.error)
                if (e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException) {
                    textError.setText("Email ou mot de passe invalide")
                }else {
                    textError.setText("Échec de connexion")
                }
            }
    }

    private fun updateUi(user: FirebaseUser?) {
        // token retourné par Firebase : utile si on doit par la suite communiquer avec un backend tierce
        //binding.textView5.setText("Bienvenue "+user.getIdToken(true));
        //binding.textView5.setText("Bienvenue " + user!!.displayName)

        /*if (user.isEmailVerified == true) binding.textView6.setText("Courriel vérifié") else binding.textView6.setText(
            "Courriel non vérifié"
        )*/
        startActivity(Intent(this@ConnexionInscriptionActivity, MainActivity::class.java))
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().signOut()
    }
}