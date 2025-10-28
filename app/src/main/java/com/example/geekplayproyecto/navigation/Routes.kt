package com.example.geekplayproyecto.navigation

// Centraliza las rutas para evitar "strings mágicos"
sealed class Route(val path: String) {

    companion object {
        const val FILTER_EMAIL_ARG = "filterEmail"
    }

    // RUTA 1: INICIO PURO (HomeBase - Sin argumentos, sin conflictos)
    data object HomeBase : Route("home_base")

    //  RUTA 2: HOME FILTRADA (HomeFiltered - Con argumento opcional)
    data object HomeFiltered : Route("home_filtered?${FILTER_EMAIL_ARG}={${FILTER_EMAIL_ARG}}") {
        fun path(email: String) = "home_filtered?${FILTER_EMAIL_ARG}=${email}"
    }

    data object Login    : Route("login")
    data object Register : Route("register")

    data object Create   : Route("create")


    // Ruta con argumento path para abrir un post por id
    data object Detail   : Route("detail/{postId}") {
        fun path(id: String) = "detail/$id"
    }
    data object Profile : Route("profile")

    // Nota: La función Home.path() y Route.Home fueron sustituidas por HomeBase.path y HomeFiltered.path

}