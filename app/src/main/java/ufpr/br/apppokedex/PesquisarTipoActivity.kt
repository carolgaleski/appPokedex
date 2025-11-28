package ufpr.br.apppokedex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.model.Pokemon
import ufpr.br.apppokedex.util.NetworkUtils

class PesquisarTipoActivity : AppCompatActivity() {

    private lateinit var textoTipo: EditText
    private lateinit var botaoBuscar: Button
    private lateinit var resultadoLista: RecyclerView
    private lateinit var adapter: PokemonAdapter

    private var usuarioLogado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pesquisar_tipo)

        textoTipo = findViewById(R.id.textoTipo)
        botaoBuscar = findViewById(R.id.botaoBuscar)
        resultadoLista = findViewById(R.id.resultadoLista)

        resultadoLista.layoutManager = LinearLayoutManager(this)
        adapter = PokemonAdapter(mutableListOf())
        resultadoLista.adapter = adapter

        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado = sharedPref.getString("usuarioLogado", "").orEmpty()

        botaoBuscar.setOnClickListener {
            val tipoDigitado = textoTipo.text.toString().trim()

            if (tipoDigitado.isEmpty()) {
                Toast.makeText(this, "Digite um tipo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            buscarPokemonTipo(tipoDigitado)
        }
    }

    private fun buscarPokemonTipo(tipo: String) {
        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        endpoint.listarPokemons(usuarioLogado)
            .enqueue(object : Callback<List<Pokemon>> {
                override fun onResponse(
                    call: Call<List<Pokemon>>,
                    response: Response<List<Pokemon>?>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val lista = response.body()!!
                        val filtrados = lista.filter {
                            it.tipo.equals(tipo, ignoreCase = true)
                        }

                        adapter.updateList(filtrados)

                        if (filtrados.isEmpty()) {
                            Toast.makeText(
                                this@PesquisarTipoActivity,
                                "Nnenhum Pokémon do tipo foi encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
                    Toast.makeText(
                        this@PesquisarTipoActivity,
                        "Erro ao buscar Pokémon.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}