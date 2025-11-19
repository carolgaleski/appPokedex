package ufpr.br.apppokedex.model

import com.google.gson.annotations.SerializedName

data class Usuario (
    @SerializedName("nome")
    var nome : String,
    @SerializedName("id")
    var id: String,
    @SerializedName("email")
    var email: String
)