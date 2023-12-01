package com.example.tp4.ui.inscription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tp4.MainActivity
import com.example.tp4.R
import com.example.tp4.databinding.ConnexionBinding
import com.example.tp4.databinding.InscriptionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class InscriptionFragment:Fragment() {
    private var _binding: InscriptionBinding? = null

    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null

    private lateinit var txtViewName:EditText
    private lateinit var txtViewEmail:EditText
    private lateinit var txtViewMdp:EditText
    private lateinit var textError:TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = InscriptionBinding.inflate(inflater, container, false)
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

        txtViewEmail=binding.editTextTextEmail
        txtViewMdp=binding.editTextTextPassword
        txtViewName=binding.editTextTextPersonName
        textError=binding.textErrorInscription

        binding.buttonSignup.setOnClickListener{
            if (txtViewEmail.text.toString().trim().isNullOrEmpty()||txtViewName.text.toString().trim().isNullOrEmpty()||txtViewMdp.text.toString().trim().isNullOrEmpty())
                errorWithMessage("Veuillez bien remplir tous les champs")
            else
                createAccount(txtViewName.text.toString().trim(),txtViewEmail.text.toString().trim(),txtViewMdp.text.toString().trim())
        }
        return root

    }


    // Inscription
    private fun createAccount(nom: String, courriel: String, mdp: String) {
        // mode courriel + mdp : noter qu'on ne peut pas passer un username...
        mAuth!!.createUserWithEmailAndPassword(courriel, mdp)
            .addOnCompleteListener(
                requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    // Succès, on a désormais accès a l'objet utilisateur
                    Log.d("***TAG", "createUserWithEmail:success")
                    val user = mAuth!!.currentUser

                    // ...Et on peut construire un profil associé à un utilisateur connecté
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nom) // on peut également ajouter une photo avec .setPhotoUri(Uri uri)
                        .build()
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "Profil mis à jour.")
                                Toast.makeText(
                                    requireContext(), "Bienvenue ${user.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUi(user)
                            }
                        }
                } /*else {
                    // Échec : en cas d'échec, vérifier dans le logcat l'instance d'exception lancée
                    Log.w(
                        TAG,
                        "createUserWithEmail:failure",
                        task.exception
                    )
                    Toast.makeText(
                        this@SignUpActivity, "Échec de l'Authentication.",
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
            }
            .addOnFailureListener { e ->
                if(e is FirebaseAuthWeakPasswordException)
                    errorWithMessage("Le mot de passe est trop faible. Il doit contenir au moins 6 caractères.")
                else if(e is FirebaseAuthInvalidCredentialsException)
                    errorWithMessage("Adresse e-mail invalide.")
                else if(e is FirebaseAuthUserCollisionException)
                    errorWithMessage("Un compte avec cette adresse e-mail existe déjà.")
                else
                    errorWithMessage("Échec de l'inscription. Veuillez réessayer plus tard.")
            }
        // Ici, on pourrait ajouter un callback .addOnFailureListener
    }


    private fun errorWithMessage(msgError:String){
        textError.setPadding(20,20,20,20)
        textError.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.error)
        textError.setText(msgError)
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

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().signOut()
    }

}