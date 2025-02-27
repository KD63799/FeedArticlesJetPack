package com.example.feedarticlesjetpack.Network

object ApiRoutes {
    const val BASE_URL = "https://formation.dev2.dev-id.fr/api/"

    // Les endpoints pour register et login doivent inclure "articles/"
    const val REGISTER = "articles/user/"
    const val LOGIN = "articles/user/"

    // Pour les articles (les autres endpoints restent inchangés)
    const val GET_ALL_ARTICLES = "articles/"
    const val GET_ARTICLE = "articles/" // en utilisant l'ID dans le path
    const val ADD_ARTICLE = "articles/"
    const val UPDATE_ARTICLE = "articles/" // idem, avec l’ID dans le Path
    const val DELETE_ARTICLE = "articles/"

    const val AUTH_PARAM = "token"
}
