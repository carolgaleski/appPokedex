package ufpr.br.apppokedex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ufpr.br.apppokedex.model.Pokemon
import ufpr.br.apppokedex.model.PokemonResponse
import ufpr.br.apppokedex.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufpr.br.apppokedex.api.Endpoint


class PesquisarHabilidadeActivity : AppCompatActivity() {

    private lateinit var pesHabilidade: EditText
    private lateinit var botaoBuscar: Button
    private lateinit var resultadoLista: RecyclerView
    private lateinit var adapter: PokemonAdapter

    private var usuarioLogado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pesquisar_habilidade)

        pesHabilidade = findViewById(R.id.pesHabilidade)
        botaoBuscar = findViewById(R.id.botaoBuscar)
        resultadoLista = findViewById(R.id.resultadoLista)

        resultadoLista.layoutManager = LinearLayoutManager(this)
        adapter = PokemonAdapter(mutableListOf())
        resultadoLista.adapter = adapter

        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado = sharedPref.getString("usuarioLogado", "").orEmpty()

        botaoBuscar.setOnClickListener {
            val hab = pesHabilidade.text.toString().trim()
        if (hab.isEmpty()) {
            Toast.makeText(this, "Digite uma habilidade", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        buscarPorHabilidade(hab)
        }

    }

    private fun buscarPorHabilidade(habilidade: String) {
        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        endpoint.listarPokemons(usuarioLogado)
            .enqueue(object : Callback<List<Pokemon>> {
                override fun onResponse(call: Call<List<Pokemon>>, response: Response<List<Pokemon>>) {
                    if (response.isSuccessful && response.body() != null) {

                        val lista = response.body()!!
                        val filtrados = lista.filter { pokemon ->
                            pokemon.habilidades.any { it.equals(habilidade, ignoreCase = true) }
                        }

                        adapter.updateList(filtrados)

                        if (filtrados.isEmpty()) {
                            Toast.makeText(
                                this@PesquisarHabilidadeActivity,
                                "Nenhum Pokémon possui essa habilidade.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }

                override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
                    Toast.makeText(this@PesquisarHabilidadeActivity, "Erro ao buscar", Toast.LENGTH_SHORT).show()
                }
            })
    }
            }