package com.example.tp4.modele

import com.example.tp4.data.categories
import java.util.*

data class Item(
    var id: String? = null,
    val nom: String? = null,
    var categorie: categories,
    val prix: Double,
    val date: Date? = null,
    var quantite: Int,

    val description: String? = null,
)
{
    // Ajoutez un constructeur sans argument
    constructor() : this("", null, categories.Fruit, 0.0, null, 1, null)
}