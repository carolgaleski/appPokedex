package ufpr.br.apppokedex.model

data class PokemonRequest(
    val nome: String,
    val tipo: String,
    val habilidades: List<String>,
    val usuario: String
)
