package com.example.geekplayproyecto.utils

import com.example.geekplayproyecto.R

object ImageMapper {
    fun getDrawableIdFromName(imageName: String?): Int? {
        if (imageName == null) return null

        // Si empieza con "res:", significa que es una imagen interna (precargada)
        if (imageName.startsWith("res:")) {
            return when (imageName.substringAfter("res:")) {
                // Clave del Servidor  ->  Tu archivo en res/drawable
                "valorant"   -> R.drawable.valorant_videojuego
                "kny"        -> R.drawable.kny_pelicula
                "twd"        -> R.drawable.twd_serie
                "spiderman"  -> R.drawable.spiderman_comic
                "admin"      -> R.drawable.spiderman_comic // Foto por defecto para el admin
                else -> null
            }
        }
        return null
    }
}