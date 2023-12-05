package com.example.tp4.data

enum class categories(val etat: String) {
    Fruit("fruit"),
    Legume("Légume"),
    Montre("Montre");
    companion object {
        fun fromEtat(etat: String): categories? {
            return values().find { it.etat == etat }
        }
    }
}

