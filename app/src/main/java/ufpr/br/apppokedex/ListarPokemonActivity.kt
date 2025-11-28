package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.model.Pokemon
import ufpr.br.apppokedex.util.NetworkUtils

class ListarPokemonActivity : AppCompatActivity() {

    private lateinit var rvPokemons: RecyclerView
    private lateinit var adapter: PokemonAdapter
    private var usuarioLogado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_pokemon)

        rvPokemons = findViewById(R.id.rvPokemons)
        rvPokemons.layoutManager = LinearLayoutManager(this)

        // inicialização do adapter com os callbacks para editar ou excluir (para ser possível gerenciar apenas na listarPokemonActivity)
        adapter = PokemonAdapter(
            mutableListOf(),
            onEditClick = { pokemon -> editarPokemon(pokemon) },
            onDeleteClick = { pokemon -> confirmarExclusao(pokemon) }
        )
        rvPokemons.adapter = adapter

        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado = sharedPref.getString("usuarioLogado", "").orEmpty()
    }

    override fun onResume() {
        super.onResume()
        carregarPokemons()
    }

    private fun carregarPokemons() {
        if (usuarioLogado.isEmpty()) return

        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        endpoint.listarPokemons(usuarioLogado)
            .enqueue(object : Callback<List<Pokemon>> {
                override fun onResponse(
                    call: Call<List<Pokemon>>,
                    response: Response<List<Pokemon>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val lista = response.body()!!
                        adapter.updateList(lista)
                    } else {
                        adapter.updateList(emptyList())
                        Toast.makeText(this@ListarPokemonActivity, "Falha ao carregar Pokémon", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
                    adapter.updateList(emptyList())
                    Toast.makeText(this@ListarPokemonActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun editarPokemon(pokemon: Pokemon) {
        val intent = Intent(this, CadastroPokemonActivity::class.java)
        intent.putExtra("pokemon_id", pokemon.id)
        intent.putExtra("pokemon_nome", pokemon.nome)
        intent.putExtra("pokemon_tipo", pokemon.tipo)
        intent.putStringArrayListExtra("pokemon_habilidades", ArrayList(pokemon.habilidades))
        startActivity(intent)
    }

    private fun confirmarExclusao(pokemon: Pokemon) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Pokémon")
            .setMessage("Deseja realmente excluir ${pokemon.nome}?")
            .setPositiveButton("Sim") { _, _ -> excluirPokemon(pokemon) }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun excluirPokemon(pokemon: Pokemon) {
        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        endpoint.excluirPokemon(pokemon.id)
            .enqueue(object : Callback<ufpr.br.apppokedex.model.PokemonResponse> {
                override fun onResponse(
                    call: Call<ufpr.br.apppokedex.model.PokemonResponse>,
                    response: Response<ufpr.br.apppokedex.model.PokemonResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ListarPokemonActivity, "Pokémon excluído com sucesso", Toast.LENGTH_SHORT).show()
                        carregarPokemons()
                    } else {
                        Toast.makeText(this@ListarPokemonActivity, "Erro ao excluir Pokémon", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ufpr.br.apppokedex.model.PokemonResponse>, t: Throwable) {
                    Toast.makeText(this@ListarPokemonActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

