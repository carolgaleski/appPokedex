package ufpr.br.apppokedex.model

data class PokemonResponse(
    val sucesso: Boolean? = null,
    val mensagem: String? = null,
    val erro: String? = null
)
