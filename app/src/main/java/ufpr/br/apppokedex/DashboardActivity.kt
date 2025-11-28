package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.system.exitProcess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.model.Pokemon
import ufpr.br.apppokedex.util.NetworkUtils

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserNome: TextView
    private lateinit var totalPokemon: TextView
    private lateinit var top3tipos: TextView
    private lateinit var top3habilidades: TextView
    private lateinit var listaPokemon: RecyclerView

    private var usuarioLogado: String = ""
    private var nomeUsuario: String = "Usuário"

    private lateinit var adapter: PokemonAdapter

    private val cadastroPokemonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            carregarPokemon()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvUserNome = findViewById(R.id.tvUserNome)
        totalPokemon = findViewById(R.id.totalPokemon)
        top3tipos = findViewById(R.id.top3tipos)
        top3habilidades = findViewById(R.id.top3habilidades)
        listaPokemon = findViewById(R.id.listaPokemon)

        listaPokemon.layoutManager = LinearLayoutManager(this)

        // pra não perder as prefs. do user
        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado = sharedPref.getString("usuarioLogado", "").orEmpty()
        nomeUsuario = sharedPref.getString("nomeUsuario", "Usuário").orEmpty()
        tvUserNome.text = "Bem-vindo, $nomeUsuario"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //pokemon adapter com a lista vazia
        adapter = PokemonAdapter(mutableListOf())
        listaPokemon.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        carregarPokemon()
    }

    private fun carregarPokemon() {
        if (usuarioLogado.isEmpty()) return

        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        endpoint.listarPokemons(usuarioLogado)
            .enqueue(object : Callback<List<Pokemon>> {
                override fun onResponse(call: Call<List<Pokemon>>, response: Response<List<Pokemon>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val lista = response.body()!!

                        // atualiza o total
                        totalPokemon.text = "Pokémon cadastrados: ${lista.size}"

                        adapter.updateList(lista)

                        // exibição do top 3 tipos
                        val tiposCount = lista.groupingBy { it.tipo }.eachCount()
                        val topTipos = tiposCount.entries
                            .sortedByDescending { it.value }
                            .take(3)
                            .joinToString(", ") { it.key }
                        top3tipos.text = if (topTipos.isNotEmpty()) "Top 3 tipos: $topTipos" else "Top 3 tipos: Nenhum"

                        // exibição top 3 habilidades
                        val habilidadesCount = lista.flatMap { it.habilidades }
                            .groupingBy { it }
                            .eachCount()
                        val topHabilidades = habilidadesCount.entries
                            .sortedByDescending { it.value }
                            .take(3)
                            .joinToString(", ") { it.key }
                        top3habilidades.text = if (topHabilidades.isNotEmpty()) "Top 3 habilidades: $topHabilidades" else "Top 3 habilidades: Nenhuma"

                    } else {
                        totalPokemon.text = "Pokemon cadastrados: 0"
                        adapter.updateList(emptyList())
                        top3tipos.text = "Top 3 tipos: nenhum"
                        top3habilidades.text = "Top 3 habilidades: nenhuma"
                    }
                }

                override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
                    totalPokemon.text = "Pokemon cadastrados: 0"
                    adapter.updateList(emptyList())
                    top3tipos.text = "Top 3 tipos: nenhum"
                    top3habilidades.text = "Top 3 habilidades: nenhuma"
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNovo -> cadastroPokemonLauncher.launch(Intent(this, CadastroPokemonActivity::class.java))
            R.id.menuListar -> startActivity(Intent(this, ListarPokemonActivity::class.java))
            R.id.menuPesquisarTipo -> startActivity(Intent(this, PesquisarTipoActivity::class.java))
            R.id.menuPesquisarHab -> startActivity(Intent(this, PesquisarHabilidadeActivity::class.java))
            R.id.menuSair -> {
                finishAffinity()
                exitProcess(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
