package ufpr.br.apppokedex.model

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int,
    val nome: String,
    val tipo: String,
    val habilidades: List<String>,

    @SerializedName("usuario_criador")
    val usuario: String
)
