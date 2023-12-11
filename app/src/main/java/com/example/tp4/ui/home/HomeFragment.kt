package com.example.tp4.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp4.MainActivity
import com.example.tp4.databinding.FragmentHomeBinding
import com.example.tp4.modele.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.tp4.modele.itemAdapter
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var itemList: MutableList<Item>
    private lateinit var adapterItem: itemAdapter
    private val binding get() = _binding!!
    // Connexion à la base de données Firestore
    var db = FirebaseFirestore.getInstance()

    // Cr est un alias pour la collection "todos"
    var Cr = db.collection("items")

    // Le listener qui permet de surveiller les changements dans la collection
    // est enregistré dans cette variable afin de pouvoir le désactiver dans le onStop()
    var registration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        itemList= ArrayList<Item>()


        adapterItem = itemAdapter(itemList)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvItem.layoutManager= LinearLayoutManager(requireContext())
        binding.rvItem.setHasFixedSize(true)


        // Création de l'écouteur d'événement pour le RecyclerView
        // (voir la classe PersonAdapter) interface OnItemClickListenerInterface
            val onItemClickListener: itemAdapter.OnItemClickListenerInterface =
            object : itemAdapter.OnItemClickListenerInterface {


                // Méthode appelée lors du clic sur le bouton Éditer
                override fun onClickEdit(itemView: View, position: Int) {
                    val dialog=CreerItem()
                    val args = Bundle()
                    var item=itemList!![position]
                    args.putString("nom", item.nom)
                    args.putInt("qte", item.quantite)

                    args.putString ("id", item.id)
                    args.putString("description", item.description)
                    args.putString("categorie", item.categorie.etat)
                    args.putDouble("prix", item.prix)
                    dialog.arguments = args
                    // FragmentManager pour afficher le fragment de dialogue
                    val fm: FragmentManager =  MainActivity.fm
                    dialog.show(fm, "fragment_edit_name")
                }

                // Méthode appelée lors du clic sur le bouton Supprimer
                override fun onClickDelete(position: Int) {
                    //val itemPosition = viewHolder.adapterPosition
                    adapterItem.notifyItemRemoved(position)

                    // Suppression de l'enregistrement de Firebase grâce à son id
                    Cr.document(itemList[position].id!!).delete()
                }
            }

        adapterItem.setOnItemClickListener(onItemClickListener)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.rvItem.adapter=adapterItem


        /**
         * Gestion du swipe à gauche pour supprimer un enregistrement
         */
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val itemPosition = viewHolder.adapterPosition
                    adapterItem.notifyItemRemoved(itemPosition)

                    // Suppression de l'enregistrement de Firebase grâce à son id
                    Cr.document(itemList[itemPosition].id!!).delete()
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvItem)

    }


    override fun onStart() {
        super.onStart()
        /**
         * Ajout d'un listener sur la collection "Item"
         * Récupération des enregistrements de la collection en ordre de date décroissante
         * Le listener est appelé à chaque fois qu'un enregistrement est ajouté, modifié ou supprimé
         */
        registration = Cr
            .orderBy(KEY_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                Log.d("TAG", "onEvent: $value")
                itemList.clear()
                for (document in value!!) {
                    // Récupération de l'enregistrement sous forme d'objet Item
                    val item = document.toObject(Item::class.java)
                    // Ajout de l'id dans l'objet Item
                    // Sera utilisé pour la suppression
                    // document.id récupère l'id de l'enregistrement de Firebase
                    item.id = document.id
                    itemList.add(item)
                }
                adapterItem.notifyDataSetChanged()
            }
    }

    override fun onStop() {
        super.onStop()
        registration!!.remove()
    }
    companion object {
        private const val KEY_TIME = "date"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}