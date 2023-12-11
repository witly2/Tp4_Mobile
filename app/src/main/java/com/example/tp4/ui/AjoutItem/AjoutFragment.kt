package com.example.tp4.ui.AjoutItem

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.tp4.App
import com.example.tp4.ConnexionInscriptionActivity
import com.example.tp4.data.categories
import com.example.tp4.databinding.FragmentAjoutBinding
import com.example.tp4.modele.Item
import com.example.tp4.ui.connexion.ConnexionFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AjoutFragment : Fragment() , View.OnClickListener{

    private var _binding:FragmentAjoutBinding?=null
    lateinit var nom: EditText
    lateinit var description: EditText
    lateinit var prix: EditText
    private var mCategoriesDeBase: categories = categories.values()[0]
    private val binding get() = _binding!!




    // Connexion à la base de données Firestore
    var db = FirebaseFirestore.getInstance()

    // Cr est un alias pour la collection "todos"
    var Cr = db.collection("items")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAjoutBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val spinner: Spinner = binding.spinner
        val listcategorie= mutableListOf<String>()
        for(categorie in categories.values())
            listcategorie.add(categorie.etat)

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, listcategorie)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        nom=binding.etName
        description=binding.etDescription
        prix=binding.etPrix

        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItemName = parent.getItemAtPosition(position).toString()

                val categories = categories.fromEtat(selectedItemName)

                if (categories != null) {
                    mCategoriesDeBase=categories
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }
        binding.btnAjout.setOnClickListener(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     *
     * Methode onclick
     * */
    override fun onClick(v: View) {
        if (v === binding.btnAjout) {
            Log.d("TAG", "onClick: ")
            val nom = nom.text.toString().trim { it <= ' ' }
            val prix = prix.text.toString().trim { it <= ' ' }
            val categorie = mCategoriesDeBase
            val date = Date()
            val description = binding.etDescription.text.toString().trim { it <= ' ' }

            if(nom.isNullOrEmpty()||description.isNullOrEmpty()||prix.isNullOrEmpty()||prix.toDouble()==0.0) {
                Toast.makeText(requireContext(), "L'item n'a pas été créé. Informations insuffisantes ou mal entrées", Toast.LENGTH_SHORT).show()
            }
            else{
                val item = Item("", nom =nom, categorie = categorie, prix = prix.toDouble(), date =date, quantite = 1, description = description)

                // Ajoute un nouveau Item à la collection "items"
                Cr
                    .add(item)
                    .addOnSuccessListener {
                        Log.d("TAG","Ajout réussi")

                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error adding document", e) }


                    //Retour sur le fragment Home
                    val action= AjoutFragmentDirections.actionAjoutHome()
                    Navigation.findNavController(v).navigate(action)
                    Toast.makeText(requireContext(), "Vous êtes déjà connecté.", Toast.LENGTH_SHORT).show()

                    notificationAjout()

            }

        }
    }


    @SuppressLint("MissingPermission")
    private fun notificationAjout() {

        // Création d'un intent pour lancer l'activité MainActivity
        // lorsque l'utilisateur clique sur la notification.
        val intent = Intent(requireContext(), ConnexionInscriptionActivity::class.java)
        /*
         PendingIntent est un objet d'encapsulation d'un Intent
         Permet à l'Intent inclus de s'exécuter même lorsque l'app n'est plus lancée
         Peut lancer : une activité, un broadcast, un service
         Ici : lancera l'application qui a envoyé la notification
         */
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Création d'une notification.
        var builder = NotificationCompat.Builder(requireContext(), App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_input_add)
            .setContentTitle("Ajout d'article tp4")
            .setContentText("Vous avez bien ajouté un item")
            .setContentIntent(pendingIntent) // lance l'activité MainActivity si on clic sur la notif
            .setAutoCancel(true) // supprime la notif quand on clic dessus
            // La catégorie permet au système de déterminer comment afficher la notification
            // si le mode "ne pas déranger" est activé.
            // https://developer.android.com/develop/ui/views/notifications/build-notification#system-category
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        // Envoi de la notification.
        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, builder.build())
        }


    }
}