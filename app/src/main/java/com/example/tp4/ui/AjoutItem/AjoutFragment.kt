package com.example.tp4.ui.AjoutItem

import android.R
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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
    var descriptionV:String? = null
    private var mCategoriesDeBase: categories = categories.values()[0]
    var idV:Int = 0
    private val binding get() = _binding!!




    // Connexion à la base de données Firestore
    var db = FirebaseFirestore.getInstance()

    // Cr est un alias pour la collection "todos"
    var Cr = db.collection("items")

//    // Le listener qui permet de surveiller les changements dans la collection
//    // est enregistré dans cette variable afin de pouvoir le désactiver dans le onStop()
//    var registration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(AjoutViewModel::class.java)

        _binding = FragmentAjoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

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
        //var categorieDeBase=categorie.values()[0]
//        arguments?.let {
//            val name = it.getString("nom")
//            idV=it.getInt("id")
//            descriptionV=it.getString("description")
//            val categorieV=it.getString("categorie")
//            val prixV=it.getDouble("prix")
//            if(!name.isNullOrEmpty()){
//                nom.setText(name)
//                description.setText(descriptionV)
//                val v=categorie.fromEtat(categorieV!!)
//                spinner.setSelection(v!!.ordinal)
//                categorieDeBase=v
//                prix.setText(prixV.toString())
//            }
//        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItemName = parent.getItemAtPosition(position).toString()

                val categories = categories.fromEtat(selectedItemName)

                if (categories != null) {
                    mCategoriesDeBase=categories
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
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
            //val item = Item("", nom =nom, categorie = categorie, prix = prix.toDouble(), date =date, quantite = 1, description = description)

            if(nom.isNullOrEmpty()||description.isNullOrEmpty()||prix.isNullOrEmpty()||prix.toDouble()==0.0) {
                Toast.makeText(requireContext(), "Le vendeur n'a pas été créé. Informations insuffisantes ou mal entrées", Toast.LENGTH_SHORT).show()
            }
            else{
                val item = Item("", nom =nom, categorie = categorie, prix = prix.toDouble(), date =date, quantite = 1, description = description)

                // Ajoute un nouveau Item à la collection "todos"
                Cr
                    .add(item)
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot added with ID: ") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error adding document", e) }

                val action= AjoutFragmentDirections.actionAjoutHome()
                Navigation.findNavController(v).navigate(action)
//
//                var builder = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
//                    .setSmallIcon(R.drawable.ic_android)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setContentIntent(pendingIntent) // lance l'activité MainActivity si on clic sur la notif
//                    .setAutoCancel(true)

                //Retour sur le fragment Home
                // Obtenez le NavController
                val navController = NavHostFragment.findNavController(this@AjoutFragment)

                // Effectuez la navigation vers le fragment HomeFragment
                //navController.navigate(R.id.home)
            }

        }
    }


//    private fun sendOnChannel1() {
//        val title: String = binding.editTextTitle.text.toString()
//        val message: String = binding.editTextMessage.text.toString()
//
//        // Création d'un intent pour lancer l'activité MainActivity
//        // lorsque l'utilisateur clique sur la notification.
//        val intent = Intent(this, MainActivity::class.java)
//        /*
//         PendingIntent est un objet d'encapsulation d'un Intent
//         Permet à l'Intent inclus de s'exécuter même lorsque l'app n'est plus lancée
//         Peut lancer : une activité, un broadcast, un service
//         Ici : lancera l'application qui a envoyé la notification
//         */
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        // Création d'une notification.
//        var builder = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
//            .setSmallIcon(R.drawable.ic_android)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setContentIntent(pendingIntent) // lance l'activité MainActivity si on clic sur la notif
//            .setAutoCancel(true) // supprime la notif quand on clic dessus
//            // La catégorie permet au système de déterminer comment afficher la notification
//            // si le mode "ne pas déranger" est activé.
//            // https://developer.android.com/develop/ui/views/notifications/build-notification#system-category
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//
//        // Envoi de la notification.
//        with(NotificationManagerCompat.from(this)) {
//            notify(1, builder.build())
//        }
//
//
//    }
}