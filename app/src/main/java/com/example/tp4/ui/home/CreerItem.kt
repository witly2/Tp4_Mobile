package com.example.tp4.ui.home

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tp4.R
import com.example.tp4.data.categories
import androidx.fragment.app.DialogFragment
import com.example.tp4.MainActivity
import com.example.tp4.modele.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date


class CreerItem():DialogFragment() {

    lateinit var nom: EditText
    lateinit var description: EditText
    lateinit var prix: EditText
    var descriptionV: String? = null
    var idV: Int = 0
    var quantite=0

    var db = FirebaseFirestore.getInstance()

    // Cr est un alias pour la collection "todos"
    var Cr = db.collection("items")

    // Le listener qui permet de surveiller les changements dans la collection
    // est enregistré dans cette variable afin de pouvoir le désactiver dans le onStop()
    var registration: ListenerRegistration? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = activity?.let { AlertDialog.Builder(it) }
        // Layout Inflater : Responsable de l'affichage du layout
        // requireActivity() : Servi par l'activité appelante (ici, MainActivity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_creation, null)

        val spinner: Spinner = view.findViewById(R.id.catSpinner)
        val listcategorie = mutableListOf<String>()
        for (categorie in categories.values())
            listcategorie.add(categorie.etat)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listcategorie)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        nom = view.findViewById(R.id.et_name)
        description = view.findViewById(R.id.et_description)
        prix = view.findViewById(R.id.et_prix)

        spinner.adapter = adapter
        spinner.setSelection(0)
        var categorieDeBase = categories.values()[0]
        // Titre
        arguments?.let {
            val name = it.getString("nom")
            idV = it.getInt("id")
            descriptionV = it.getString("description")
            quantite = it.getInt("qte")
            val categorieV = it.getString("categorie")
            val prixV = it.getDouble("prix")
            if (!name.isNullOrEmpty()) {
                nom.setText(name)
                description.setText(descriptionV)
                val v = categories.fromEtat(categorieV!!)
                spinner.setSelection(v!!.ordinal)
                categorieDeBase = v
                prix.setText(prixV.toString())
            }
            builder?.setTitle("Ajout d'un vendeur")
        }
        // Ajoutez un écouteur d'événements pour traiter les changements de sélection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItemName = parent.getItemAtPosition(position).toString()

                val categorie = categories.fromEtat(selectedItemName)

                if (categorie != null) {
                    categorieDeBase = categorie
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code à exécuter lorsque rien n'est sélectionné
            }
        }

        // Importe le layout de la boîte de dialogue
        // Le paramètre null est nécessaire car le layout est directement lié à la boîte de dialogue et non ancré dans un parent
        builder?.setView(view)

            // Gestion des boutons Ok et Annuler
            ?.setPositiveButton("OK") { dialog, id ->
                val nom = nom.text.toString()
                val descriptionO = description.text.toString()
                val prixO = prix.text.toString()


                val date = Date()
                val item = Item("", nom =nom, categorie = categorieDeBase, prix = prixO.toDouble(), date =date, quantite, description = descriptionO)

                if (nom.isNullOrEmpty() || descriptionO.isNullOrEmpty() || prixO.isNullOrEmpty() || prixO.toDouble() == 0.0) {
                    Toast.makeText(
                        requireContext(),
                        "Le vendeur n'a pas été créé. Informations insuffisantes ou mal entrées",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {

                    Cr.document(idV.toString()).update("description",item)
                        .addOnSuccessListener {
                            // La mise à jour a réussi
                            Log.d(TAG, "Mise à jour réussie!")
                        }
                        .addOnFailureListener { e ->
                            // La mise à jour a échoué
                            Log.w(TAG, "Erreur lors de la mise à jour", e)
                        }
                }

            }
            ?.setNegativeButton("Annuler") { dialog, id ->
                getDialog()?.cancel()
            }
        if (builder != null) {
            return builder.create()
        }
        return super.onCreateDialog(savedInstanceState)
    }
}