package com.example.tp4.ui.connexion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tp4.MainActivity
import com.example.tp4.R
import com.example.tp4.databinding.ConnexionBinding
import com.example.tp4.ui.inscription.InscriptionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser


class ConnexionFragment: Fragment(),View.OnClickListener {
    private var _binding: ConnexionBinding? = null
    private lateinit var textViewEmail:TextView
    private lateinit var textViewPassword:TextView
    private lateinit var textError:TextView
    private var mAuth: FirebaseAuth? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ConnexionBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Instance du service d'authentification
        mAuth = FirebaseAuth.getInstance()

        // Si token user déjà valide
        if (mAuth!!.currentUser != null) {
            Log.d("***TAG", "onCreate: " + mAuth!!.currentUser)
            //Rediriger vers l'accueil

            Toast.makeText(requireContext(), "Vous êtes déjà connecté.", Toast.LENGTH_SHORT).show()
            updateUi(mAuth!!.currentUser)
        }


        textViewEmail=binding.editTextTextEmail
        textViewPassword=binding.editTextTextPassword
        textError=binding.textError
        binding.textView2.setOnClickListener(this)

        // Connexion
        binding.buttonConnexion.setOnClickListener(this)


        return root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textView2 -> {
                val action= ConnexionFragmentDirections.actionNavConnexionToNavInscription()
                Navigation.findNavController(v).navigate(action)
                //startActivity(Intent(requireContext(), InscriptionFragment::class.java))
            }
            R.id.button_connexion -> {
                if(textViewEmail.getText().toString().trim().isNullOrEmpty()||textViewPassword.getText().toString().trim().isNullOrEmpty())
                    errorWithMessage("Veuillez remplir tous les champs")
                else
                    signIn(textViewEmail.getText().toString().trim(),
                        textViewPassword.getText().toString().trim())
            }
        }
    }

    private fun signIn(courriel: String, mdp: String) {

        mAuth!!.signInWithEmailAndPassword(courriel, mdp)
            .addOnCompleteListener(
                requireActivity()
            ) { task ->
                Toast.makeText(
                    requireContext(), "Connexion en cours ...",
                    Toast.LENGTH_SHORT,
                ).show()
                if (task.isSuccessful) {
                    // Firebase User est un user connecté
                    val user = mAuth!!.currentUser
                    Toast.makeText(
                        requireContext(), "Bienvenue ${user!!.displayName}",
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
               if (e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException) {
                    errorWithMessage("Email ou mot de passe invalide")
                }else {
                   errorWithMessage("Échec de connexion")
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
          startActivity(Intent(requireContext(), MainActivity::class.java))
      }
    private fun errorWithMessage(msgError:String){
        textError.setPadding(20,20,20,20)
        textError.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.error)
        textError.setText(msgError)
    }
      override fun onStop() {
          super.onStop()
          FirebaseAuth.getInstance().signOut()
      }
  }